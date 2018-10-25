package org.loguno.test;

import org.loguno.Loguno;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author Dmitrii Ponomarev
 */
@Loguno.Logger
public class HelloKitty {

    private String name;
    private String owner;
    private String result;

    @Loguno
    public String sayHello() {
        return "hello";
    }

    public HelloKitty sayName(@Loguno String name) throws @Loguno IOException, @Loguno NumberFormatException {

        int i1 = Integer.parseInt("dsd");
        Files.createDirectory(null, null);

        this.name = name;
        return this;
    }

    @Loguno
    public HelloKitty sayOwner(String owner) {
        this.owner = owner;

        if (this.owner.isEmpty()) {
            this.owner = "na";
        }

        try {
            int i1 = Integer.parseInt("1");
            Files.createDirectory(null, null);
        } catch (NullPointerException | @Loguno("Num") NumberFormatException | @Loguno("IO") @Loguno IOException e) {
            e.printStackTrace();
        }


        try {
            int i1 = Integer.parseInt("aaa");
        } catch (@Loguno.DEBUG("number is wrong") NumberFormatException | @Loguno("NPE") @Loguno.DEBUG NullPointerException e) {
            // e.printStackTrace();
        }


        @Loguno
        @Loguno.INFO
        @Loguno.DEBUG
        @Loguno.WARN
        @Loguno.TRACE
        @Loguno.ERROR
        StringBuilder newName = new StringBuilder(name + " of " + this.owner);

        for (int i = 0; i < 10; i++) {
            newName.append("a").append(i);
        }

        int i = 5;

        @Loguno.TRACE
        int s = i + 1;

        return this;
    }

    @Loguno
    public static void main(String[] args) throws Exception {
        HelloKitty k = new HelloKitty();
        k.sayHello();
        k.sayName("Barsik")
                .sayOwner("Vovochka");
    }

}
