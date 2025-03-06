package org.example;

public class AbstractFactoryProducer { // it is responsible for producing an instance of UserFactory.
    //it provides a static method to get a concrete implementation of the UserFactory.
    public static UserFactory getFactory() {
        return new ConcreteUserFactory();
    }
}
