module com.rental {
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.fxml;
    requires org.mongodb.driver.sync.client;
    requires transitive org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires org.slf4j;
    requires kernel;
    requires layout;
    requires xmlrpc.client;
    requires xmlrpc.common;
    requires java.xml; // often needed by xmlrpc

    opens com.rental.app to javafx.fxml;

    exports com.rental.app;

    exports com.rental.controller;

    opens com.rental.controller to javafx.fxml;

    exports com.rental.model;
    exports com.rental.model.enums;
    exports com.rental.service;

    // Allow POJO codec to access private fields if needed (open)
    opens com.rental.model to org.mongodb.bson;
}
