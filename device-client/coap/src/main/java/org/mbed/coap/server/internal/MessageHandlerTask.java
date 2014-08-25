/**
 * Copyright (C) 2011-2014 ARM Limited. All rights reserved.
 */
package org.mbed.coap.server.internal;

import org.mbed.coap.CoapPacket;
import org.mbed.coap.exception.CoapException;
import org.mbed.coap.transport.TransportContext;
import org.mbed.coap.utils.HexArray;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Worker class that handles incoming traffic.
 *
 * @author szymon
 */
final class MessageHandlerTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandlerTask.class);
    private byte[] packet;
    private final InetSocketAddress address;
    private final TransportContext transportContext;
    private final CoapServerAbstract coapServer;

    MessageHandlerTask(ByteBuffer buffer, InetSocketAddress inetSocketAddress, TransportContext transportContext, final CoapServerAbstract coapServer) {
        this.coapServer = coapServer;
        packet = new byte[buffer.position()];
        System.arraycopy(buffer.array(), 0, packet, 0, buffer.position());
        address = inetSocketAddress;
        this.transportContext = transportContext;
    }

    @Override
    public void run() {
        try {
            CoapPacket coapPkt = CoapPacket.read(packet, packet.length, address);
            coapServer.handle(coapPkt, transportContext);
        } catch (CoapException ex) {
            LOGGER.warn("Malformed coap message source: " + address + ", size:" + packet.length);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Malformed coap message payload: " + HexArray.toHex(packet));
            }
            coapServer.handleException(packet, ex, transportContext);
        }
        
        //(hopefully) free memory by removing reference
        packet = null;
    }

}