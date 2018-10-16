package org.loguno.test;

import org.loguno.Loguno;

/**
 * @author Dmitrii Ponomarev
 */
@Loguno.Logger
public class HelloKitty {

	private String name;
	private String owner;

	public String sayHello() {
		return "hello";
	}

	public HelloKitty sayName(@Loguno String name) {
		this.name = name;
		return this;
	}

	@Loguno
	public HelloKitty sayOwner(String owner) {
		this.owner = owner;

		if (this.owner.isEmpty()) {
			this.owner = "na";
		}

		@Loguno
		StringBuilder newName = new StringBuilder(name + " of " + this.owner);

		for (int i = 0; i < 10; i++) {
			newName.append("a").append(i);
		}

		int i = 5;

		@Loguno
		int s = i + 1;

		return this;
	}

	@Loguno
	public static void main(String[] args) {
		HelloKitty k = new HelloKitty();
		k.sayHello();
		k.sayName("Barsik")
				.sayOwner("Vovochka");
	}

}
