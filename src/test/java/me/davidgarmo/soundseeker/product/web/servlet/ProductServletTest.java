package me.davidgarmo.soundseeker.product.web.servlet;

import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import me.davidgarmo.soundseeker.product.config.DBConnection;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductServletTest {
    static final Logger LOGGER = LogManager.getLogger();
    static final String BASE_URL = "http://localhost:8080/api/v1/products";
    static Tomcat tomcat;

    @BeforeAll
    static void setUp() {
        try {
            tomcat = new Tomcat();
            tomcat.setPort(8080);
            tomcat.getConnector();

            String baseDir = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
            tomcat.setBaseDir(baseDir);

            File docBase = new File("src/main/webapp/");
            if (!docBase.exists()) {
                docBase = new File(".");
            }

            Context context = tomcat.addContext("", docBase.getAbsolutePath());

            Tomcat.addServlet(context, "productServlet", new ProductServlet());
            context.addServletMappingDecoded("/api/v1/products/*", "productServlet");

            tomcat.start();
            LOGGER.debug("🚀 Tomcat test server started on port 8080.");

            try (Connection connection = DBConnection.getConnection()) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute("RUNSCRIPT FROM 'classpath:init.sql'");
                }
                LOGGER.debug("✔ Database reset successfully.");
            } catch (Exception e) {
                LOGGER.error("✘ Error resetting database: {}", e.getMessage());
            }
        } catch (LifecycleException e) {
            LOGGER.error("✘ Error starting Tomcat test server: {}", e.getMessage());
        }
    }

    @BeforeAll
    static void tearDown() {
        try {
            if (tomcat != null) {
                tomcat.stop();
                tomcat.destroy();
                LOGGER.debug("💥 Tomcat test server stopped successfully.");
            }
        } catch (LifecycleException e) {
            LOGGER.error("✘ Error stopping Tomcat test server: {}", e.getMessage());
        }
    }

    @Test
    @Order(1)
    void givenAValidProduct_whenPostRequestSent_thenProductShouldBeCreated() {
        String requestBody = """
                {
                    "name": "Piano Digital Medeli CDP5200 Blanco",
                    "description": "Con un toque moderno en mente, el diseño atrae a los entusiastas de piano de cualquier tipo, sin embargo, no destaca sólo por su apariencia, las 600 voces están muy bien diseñados para capturar la dinámica y los matices de un pianista.",
                    "brand": "Medeli",
                    "price": 999.99,
                    "available": true,
                    "thumbnail": "/uploads/1743954306794.jpg",
                    "categoryId": 3
                }
                """;
        given().request().and().body(requestBody).contentType("application/json; charset=UTF-8")
                .when().post(BASE_URL)
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body(
                        "id", notNullValue(),
                        "id", equalTo(11),
                        "name", equalTo("Piano Digital Medeli CDP5200 Blanco"),
                        "description", startsWithIgnoringCase("Con"),
                        "description", containsString("el diseño atrae a los entusiastas de piano"),
                        "description", containsString("las 600 voces están muy bien diseñados"),
                        "description", endsWithIgnoringCase("pianista."),
                        "brand", equalTo("Medeli"),
                        "price", equalTo(999.99f),
                        "available", equalTo(true),
                        "thumbnail", equalTo("/uploads/1743954306794.jpg"),
                        "categoryId", equalTo(3)
                );
        LOGGER.info("\n✔ The new product was created successfully, the assigned ID is 11, the status code is 201, and the body contains the expected values.");
    }

    @Test
    @Order(2)
    void givenProductExists_whenGetRequestSent_thenProductDetailsShouldBeReturned() {
        get("/api/v1/products/{id}", 6)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(
                        "id", notNullValue(),
                        "id", equalTo(6),
                        "name", equalTo("Campana Para Timbal LP LP322 Cromado Antiguo"),
                        "description", startsWithIgnoringCase("Los"),
                        "description", containsString("El Rey del Timbal"),
                        "description", containsString("Y tiene un cáncamo auto-alineador patentado por LP"),
                        "description", endsWithIgnoringCase("diámetro."),
                        "brand", equalTo("LP"),
                        "price", equalTo(99.99f),
                        "available", equalTo(true),
                        "thumbnail", equalTo("/img/70h8timj.webp"),
                        "categoryId", equalTo(4)
                );
        LOGGER.info("\n✔ The existing product with ID 6 was found successfully, the status code is 200, and the body contains the expected values.");
    }

    @Test
    @Order(3)
    void givenProductCreated_whenGetRequestSent_thenProductDetailsShouldBeReturned() {
        get("/api/v1/products/{id}", 11)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(
                        "id", notNullValue(),
                        "id", equalTo(11),
                        "name", equalTo("Piano Digital Medeli CDP5200 Blanco"),
                        "description", startsWithIgnoringCase("Con"),
                        "description", containsString("el diseño atrae a los entusiastas de piano"),
                        "description", containsString("las 600 voces están muy bien diseñados"),
                        "description", endsWithIgnoringCase("pianista."), "brand", equalTo("Medeli"),
                        "price", equalTo(999.99f),
                        "available", equalTo(true),
                        "thumbnail", equalTo("/uploads/1743954306794.jpg"),
                        "categoryId", equalTo(3)
                );
        LOGGER.info("\n✔ The product created in the previous test with ID 11 was found successfully, the status code is 200 and the body contains the expected values.");
    }

    @Test
    @Order(4)
    void givenProductDoesNotExist_whenGetRequestSent_thenNotFoundStatusShouldBeReturned() {
        get(BASE_URL + "/{id}", 999)
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body(
                        "error", equalTo("Product not found.")
                );
        LOGGER.info("\n✔ The product with ID 999 was not found, the status code is 404, and the body contains the expected values.");
    }

    @Test
    @Order(5)
    void givenProductExists_whenGetRequestSentWithoutId_thenProductListShouldBeReturned() {
        List<Map<String, Object>> products = get(BASE_URL).as(new TypeRef<>() {
        });
        assertThat(products, hasSize(11));
        assertThat(products.get(10).size(), equalTo(8));
        assertThat(products.get(10).get("id"), equalTo(11d));
        assertThat(products.get(10).get("name"), equalTo("Piano Digital Medeli CDP5200 Blanco"));
        assertThat(products.get(10).get("description"), equalTo("Con un toque moderno en mente, el diseño atrae a los entusiastas de piano de cualquier tipo, sin embargo, no destaca sólo por su apariencia, las 600 voces están muy bien diseñados para capturar la dinámica y los matices de un pianista."));
        assertThat(products.get(10).get("brand"), equalTo("Medeli"));
        assertThat(products.get(10).get("price"), equalTo(999.99));
        assertThat(products.get(10).get("available"), equalTo(true));
        assertThat(products.get(10).get("thumbnail"), equalTo("/uploads/1743954306794.jpg"));
        assertThat(products.get(10).get("categoryId"), equalTo(3d));
        LOGGER.info("\n✔ The product list was found successfully, it contains 11 elements, the status code is 200, and the body contains the expected values.");
    }


    @Test
    @Order(6)
    void givenProductExists_whenPutRequestSet_thenProductShouldBeUpdated() {
        String requestBody = """
                {
                    "id": 11,
                    "name": "Piano Digital Medeli CDP7200 Azul",
                    "description": "Con un toque moderno en mente, el diseño atrae a los entusiastas de piano de cualquier tipo, sin embargo, no destaca sólo por su apariencia, las 600 voces están muy bien diseñados para capturar la dinámica y los matices de un pianista. Ahora con un sistema de sonido mejorado y un nuevo color azul.",
                    "brand": "Medeli",
                    "price": 1099.99,
                    "available": false,
                    "thumbnail": "/uploads/1743960080646.jpg",
                    "categoryId": 3
                }
                """;
        given().request().and().body(requestBody).contentType("application/json; charset=UTF-8")
                .when().put(BASE_URL + "/{id}", 11)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", notNullValue(),
                        "id", equalTo(11),
                        "name", equalTo("Piano Digital Medeli CDP7200 Azul"),
                        "description", startsWithIgnoringCase("Con"),
                        "description", containsString("el diseño atrae a los entusiastas de piano"),
                        "description", containsString("las 600 voces están muy bien diseñados"),
                        "description", containsString("Ahora con un sistema de sonido mejorado y un nuevo color"),
                        "description", endsWithIgnoringCase("azul."),
                        "brand", equalTo("Medeli"), "price", equalTo(1099.99f),
                        "available", equalTo(false),
                        "thumbnail", equalTo("/uploads/1743960080646.jpg"),
                        "categoryId", equalTo(3)
                );
        LOGGER.info("\n✔ The product with ID 11 was updated successfully, the status code is 200, and the body contains the expected values.");
    }

    @Test
    @Order(7)
    void givenProductCreated_whenDeleteRequestSent_thenProductShouldBeDeleted() {
        given().request()
                .when().delete(BASE_URL + "/{id}", 11)
                .then()
                .statusCode(204);
        LOGGER.info("\n✔ The product with ID 11 was deleted successfully, the status code is 204.");
    }
}