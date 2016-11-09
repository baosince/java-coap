/*
 * Copyright (C) 2011-2016 ARM Limited. All rights reserved.
 */
package org.mbed.coap.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import org.mbed.coap.exception.ReceiveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author szymon
 */
public abstract class AbstractTransportConnector implements TransportConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTransportConnector.class);
    private InetSocketAddress bindSocket;
    protected TransportReceiver transReceiver;
    protected boolean isRunning;
    protected int bufferSize = DEFAULT_BUFFER_SIZE;
    private static final int DEFAULT_BUFFER_SIZE = 1500;
    private final ThreadLocal<ByteBuffer> buffer = new ThreadLocal<>();
    private Thread readerThread;
    private final boolean initReaderThread;
    private final String readerThreadName;

    public AbstractTransportConnector(InetSocketAddress bindSocket, boolean initReaderThread, String readerThreadName) {
        this.bindSocket = bindSocket;
        this.initReaderThread = initReaderThread;
        this.readerThreadName = readerThreadName;
    }

    public AbstractTransportConnector(InetSocketAddress bindSocket, boolean initReaderThread) {
        this(bindSocket, initReaderThread, "transport-receiver");
    }

    public AbstractTransportConnector(InetSocketAddress bindSocket) {
        this(bindSocket, true);
    }

    public AbstractTransportConnector(int port) {
        this(new InetSocketAddress(port));
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    protected abstract void initialize() throws IOException;

    protected void setTransportReceiver(TransportReceiver transReceiver) {
        if (transReceiver == null) {
            throw new NullPointerException();
        }
        this.transReceiver = transReceiver;
    }

    @Override
    public void start(TransportReceiver transportReceiver) throws IOException {
        setTransportReceiver(transportReceiver);
        initialize();
        isRunning = true;

        if (initReaderThread) {
            //start reading thread
            readerThread = new Thread(this::receiveWhileRunning, readerThreadName);
            readerThread.start();
        }
    }

    private void receiveWhileRunning() {
        while (isRunning) {
            try {
                performReceive();
            } catch (ReceiveException ex) {
                LOGGER.warn(ex.getMessage());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    protected final InetSocketAddress getBindSocket() {
        return bindSocket;
    }

    @Override
    public void stop() {
        isRunning = false;
        if (readerThread != null) {
            readerThread.interrupt();
        }
    }

    /**
     * Calls transport reading method to receive new incoming message. This method may block if transport was configured
     * to use blocking mode.
     *
     * @return true if new message was received otherwise false
     * @throws ReceiveException for anticipated errors in handling the receive, see TransportReceiver.onReceive
     */
    public abstract boolean performReceive() throws ReceiveException;

    protected ByteBuffer getBuffer() {

        if (buffer.get() == null) {
            buffer.set(ByteBuffer.allocate(bufferSize));
        }
        return (ByteBuffer) buffer.get().clear();
    }

    public static byte[] createCopyOfPacketData(ByteBuffer buffer, int len) {
        byte[] packetData = new byte[len];
        System.arraycopy(buffer.array(), 0, packetData, 0, packetData.length);
        return packetData;
    }

    protected final boolean isRunning() {
        return isRunning;
    }

}
