package is.hello.gaibu.examples;

import is.hello.gaibu.homeauto.clients.NestThermostat;

public class NestExample {

    public static void main(String[] args) {
        final NestThermostat nest = NestThermostat.create("aaa");

        Boolean res = nest.deAuthorize("aaa");



    }
}
