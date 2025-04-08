package me.davidgarmo.soundseeker.product.service.impl;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import me.davidgarmo.soundseeker.product.config.DBConnection;
import me.davidgarmo.soundseeker.product.persistence.entity.Product;
import me.davidgarmo.soundseeker.product.persistence.impl.ProductDaoH2;
import me.davidgarmo.soundseeker.product.service.expection.ProductNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductServiceTest {
    static final Logger LOGGER = LogManager.getLogger();
    ProductService productService;

    private static AsciiTable getTable(Product originalProduct, Product updatedProduct) {
        AsciiTable table = new AsciiTable();
        table.addRule();
        table.addRow("Field", "Original", "Updated");
        table.addRule();
        table.addRow("ID", originalProduct.getId(), updatedProduct.getId());
        table.addRule();
        table.addRow("Name", originalProduct.getName(), updatedProduct.getName());
        table.addRule();
        table.addRow("Description", originalProduct.getDescription(), updatedProduct.getDescription());
        table.addRule();
        table.addRow("Brand", originalProduct.getBrand(), updatedProduct.getBrand());
        table.addRule();
        table.addRow("Price", originalProduct.getPrice(), updatedProduct.getPrice());
        table.addRule();
        table.addRow("Available", originalProduct.getAvailable(), updatedProduct.getAvailable());
        table.addRule();
        table.addRow("Thumbnail", originalProduct.getThumbnail(), updatedProduct.getThumbnail());
        table.addRule();
        table.addRow("Category ID", originalProduct.getCategoryId(), updatedProduct.getCategoryId());
        table.addRule();
        table.setTextAlignment(TextAlignment.LEFT);
        return table;
    }

    @BeforeEach
    void setUp() {
        try (Connection connection = DBConnection.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("RUNSCRIPT FROM 'classpath:init.sql'");
            }
            productService = new ProductService(new ProductDaoH2());
            LOGGER.debug("✔ Database reset successfully.");
        } catch (Exception e) {
            LOGGER.error("✘ Error resetting database: {}", e.getMessage());
        }
    }

    @Test
    @Order(1)
    void givenACompleteProduct_whenSaved_thenItShouldPersistInTheDatabase() {
        Product product = new Product("Corneta Doradas JBBC-1600 L Jimbao",
                "Cuerno de caza, Acción Sib, Campana: 146mm, Material: latón, Chapado: barniz, plata, níquel.",
                "Jimbao", 199.99, true, "/img/eibo6hwc.webp", 7L);

        Product savedProduct = productService.save(product);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull().isEqualTo(11L);
        assertThat(savedProduct)
                .extracting("name", "description", "brand", "price", "available", "thumbnail", "categoryId")
                .containsExactly("Corneta Doradas JBBC-1600 L Jimbao",
                        "Cuerno de caza, Acción Sib, Campana: 146mm, Material: latón, Chapado: barniz, plata, níquel.",
                        "Jimbao", 199.99, true, "/img/eibo6hwc.webp", 7L);
        assertThat(savedProduct)
                .extracting("name", "description", "brand", "price", "available", "thumbnail", "categoryId")
                .containsExactly(product.getName(), product.getDescription(), product.getBrand(),
                        product.getPrice(), product.getAvailable(), product.getThumbnail(), product.getCategoryId());
        LOGGER.info("\n✔ The placeholder product was saved successfully and the data matches the expected values.");
    }

    @Test
    @Order(2)
    void givenAnExistingProductId_whenFoundById_thenItShouldReturnTheProduct() {
        Product product = productService.findById(1L);

        assertThat(product).isNotNull();
        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product)
                .extracting("name", "description", "brand", "price", "available", "thumbnail", "categoryId")
                .containsExactly("Melódica Fire Hohner C9432174 Red-Black (9432/32)",
                        "Desde el funk hasta el reggaeton, los ritmos calientes son una parte importante de la " +
                                "experiencia. Los conjuntos funky condicen con los ritmos apasionados, el baile " +
                                "rápido y las bebidas divertidas. Por supuesto, la melódica no puede solo brindar " +
                                "el telón de fondo con sonido perfecto, también tiene que lucir adecuada para la " +
                                "situación. Con la Fire Melódica, hemos dado a nuestra melódica un nuevo aspecto " +
                                "que volará sus cabezas. Teclas negras y rojas y un cuerpo rojo y brillante con una " +
                                "estructura robusta, hermética y el sonido típico de la melódica. ¡Un diseño robusto " +
                                "para un instrumento caliente!",
                        "Hohner", 49.99, true, "/img/lg1jorfm.webp", 7L);
        LOGGER.info("\n✔ The product stored in the database was found successfully and the data matches the expected values.");
    }

    @Test
    @Order(3)
    void givenANonExistingProductId_whenFoundById_thenItShouldThrowProductNotFoundException() {
        assertThatThrownBy(() -> productService.findById(11L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("✘ Product not found with ID: 11");
        LOGGER.info("\n✔ The expected exception was thrown when the product was not found.");
    }

    @Test
    @Order(4)
    void givenTheDatabase_whenFindAll_thenItShouldReturnAllProducts() {
        List<Product> products = productService.findAll();
        assertThat(products.size()).isEqualTo(10);

        AsciiTable table = new AsciiTable();
        table.addRule();
        table.addRow("ID", "Name", "Description", "Brand", "Price", "Available", "Thumbnail", "Category ID");
        table.addRule();
        products.forEach(product -> {
            table.addRow(product.getId(), product.getName(), product.getDescription(), product.getBrand(),
                    product.getPrice(), product.getAvailable(), product.getThumbnail(), product.getCategoryId());
            table.addRule();
        });
        LOGGER.info("\n✔ All 10 products stored in the database were found successfully.");
        LOGGER.info(table.render(160));
    }

    @Test
    @Order(5)
    void givenTheDatabaseHasNoProducts_whenFindAll_thenItShouldReturnEmptyList() {
        try (Connection connection = DBConnection.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("DELETE FROM PRODUCT");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        List<Product> products = productService.findAll();
        assertThat(products).isEmpty();
        LOGGER.info("\n✔ The database has no products and the findAll method returned an empty list.");
    }

    @Test
    @Order(6)
    void givenAnExistingProduct_whenUpdated_thenItShouldPersistTheChangesInTheDatabase() {
        Product originalProduct = productService.findById(1L);

        Product productToUpdate = productService.findById(1L);
        productToUpdate.setName("Melódica Fire Sound Electra White-Blue (9432/32)");
        productToUpdate.setDescription("Con la Fire Melódica, hemos dado a nuestra melódica un nuevo aspecto que " +
                "volará sus cabezas. Teclas blanco y azul y un cuerpo azul y brillante con una estructura robusta, " +
                "hermética y el sonido típico de la melódica. ¡Un diseño robusto para un instrumento caliente!");
        productToUpdate.setBrand("Sound Electra");
        productToUpdate.setPrice(59.99);
        productToUpdate.setAvailable(false);
        productToUpdate.setThumbnail("/img/7jorfm.webp");
        productToUpdate.setCategoryId(8L);

        Product updatedProduct = productService.update(productToUpdate);

        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getId()).isEqualTo(1L);
        assertThat(updatedProduct)
                .extracting("name", "description", "brand", "price", "available", "thumbnail", "categoryId")
                .containsExactly("Melódica Fire Sound Electra White-Blue (9432/32)", "Con la Fire Melódica, hemos " +
                                "dado a nuestra melódica un nuevo aspecto que volará sus cabezas. Teclas blanco y azul y " +
                                "un cuerpo azul y brillante con una estructura robusta, hermética y el sonido típico de la " +
                                "melódica. ¡Un diseño robusto para un instrumento caliente!", "Sound Electra", 59.99, false,
                        "/img/7jorfm.webp", 8L);
        assertThat(updatedProduct)
                .extracting("name", "description", "brand", "price", "available", "thumbnail", "categoryId")
                .containsExactly(productToUpdate.getName(), productToUpdate.getDescription(),
                        productToUpdate.getBrand(), productToUpdate.getPrice(), productToUpdate.getAvailable(),
                        productToUpdate.getThumbnail(), productToUpdate.getCategoryId());

        AsciiTable table = getTable(originalProduct, updatedProduct);
        LOGGER.info("\n✔ The product was updated successfully and the data matches the expected values.");
        LOGGER.info(table.render());
    }

    @Test
    @Order(7)
    void givenAnExistingProductId_whenDeleted_thenItShouldBeRemovedFromTheDatabase() {
        int initialCount = productService.findAll().size();

        productService.delete(1L);

        int finalCount = productService.findAll().size();

        assertThat(finalCount).isEqualTo(initialCount - 1);
        LOGGER.info("\n✔ The product was deleted successfully and the record count decreased by 1.");
    }
}