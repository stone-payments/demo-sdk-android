package br.com.stonesdk.sdkdemo.activities;

import java.util.Collections;

import br.com.stone.posandroid.net.client.api.NetworkingClient;
import br.com.stone.posandroid.net.client.api.NetworkingClientFactory;
import br.com.stone.posandroid.net.client.domain.model.ConnectionType;
import br.com.stone.posandroid.net.client.domain.model.NetClientConfig;

/**
 * NetworkingClientFactory e NetClientConfig sao "internal" no Kotlin do netclient, entao o Kotlin do
 * app nao consegue referencia-los. No bytecode sao publicos, e o Java ignora o "internal".
 * Uso restrito a ferramenta de diagnostico (NetClientTestActivity).
 */
final class NetClientFactoryHelper {
    private NetClientFactoryHelper() {
    }

    static NetworkingClient create(long connectMs, long readMs, long writeMs, long socketMs) {
        NetClientConfig config = new NetClientConfig(connectMs, readMs, writeMs, socketMs, Collections.emptyList());
        return NetworkingClientFactory.INSTANCE.create(ConnectionType.DEFAULT, config);
    }
}
