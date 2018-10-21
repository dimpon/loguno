package org.loguno.test;

import org.loguno.Loguno;
import org.loguno.test.exceptions.CaughtByPythonException;
import org.loguno.test.exceptions.FallDownException;
import org.loguno.test.exceptions.NoBananasException;

import java.util.function.Function;

@Loguno.Logger
public class JustMonkeyWithExceptions extends Animal {

    private MonkeyAction<NoBananasException> eat;
    private MonkeyAction<FallDownException> jump;
    private MonkeyAction<CaughtByPythonException> tease;


    public JustMonkeyWithExceptions(String name) {
        super(name);
    }

    public static JustMonkeyWithExceptions of(String name){
        return new JustMonkeyWithExceptions(name);
    }

    public void day1() {

        try {
            jump();
            eat();
            teasePython();
        } catch (@Loguno.WARN("Monkey is hungry") @Loguno NoBananasException |
                @Loguno.TRACE @Loguno.ERROR("Monkey is killed") CaughtByPythonException e) {

        } catch (@Loguno.INFO("Monkey hurt") @Loguno.WARN FallDownException e1) {
        }

    }

    public void day2() {
        try {
            jump();
            eat();
            teasePython();
        } catch (@Loguno.INFO("Monkey gets hurt") @Loguno FallDownException e) {

        } catch (@Loguno.WARN("Monkey is hungry") NoBananasException e) {

        } catch (@Loguno.ERROR("Monkey is killed") @Loguno.TRACE CaughtByPythonException e) {

        }
    }

    public void day3() {
        try {
            jump();
            //eat();
            //teasePython();
        } catch (@Loguno.INFO("monkey gets hurt") FallDownException e) {

        }
    }

    public void dayZ() {

        try {
            jump();
            //eat();
            //teasePython();
        } catch (@Loguno.INFO("monkey gets hurt") FallDownException e) {
            try {
                System.out.printf("poor monkey");
            } catch (@Loguno.INFO Exception e1) {

            }
        }

        try {
            jump();
            eat();
            teasePython();
        } catch (@Loguno.INFO("Monkey gets hurt") @Loguno FallDownException e) {

        } catch (@Loguno.WARN("Monkey is hungry") NoBananasException e) {

        } catch (@Loguno.ERROR("Monkey is killed") @Loguno.TRACE CaughtByPythonException e) {

        }

        try {
            jump();
            eat();
            teasePython();
        } catch (@Loguno.WARN("Monkey is hungry") @Loguno NoBananasException |
                @Loguno.TRACE @Loguno.ERROR("Monkey is killed") CaughtByPythonException e) {

        } catch (@Loguno.INFO("Monkey hurt") @Loguno.WARN FallDownException e1) {
        }

    }

    private void jump() throws FallDownException {
        jump.doSmth();
    }

    private void eat() throws NoBananasException {
        eat.doSmth();
    }

    private void teasePython() throws CaughtByPythonException {
        tease.doSmth();
    }

    public JustMonkeyWithExceptions setEat(MonkeyAction<NoBananasException> eat) {
        this.eat = eat;
        return this;
    }

    public JustMonkeyWithExceptions setJump(MonkeyAction<FallDownException> jump) {
        this.jump = jump;
        return this;
    }

    public JustMonkeyWithExceptions setTease(MonkeyAction<CaughtByPythonException> tease) {
        this.tease = tease;
        return this;
    }

    //@Loguno.Logger
    @FunctionalInterface
    interface MonkeyAction<E extends Throwable> {
        void doSmth() throws E;
    }

}
