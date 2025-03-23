package me.davidgarmo.soundseeker.product.service.impl;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import me.davidgarmo.soundseeker.product.config.DBConnection;
import me.davidgarmo.soundseeker.product.persistence.entity.Product;
import me.davidgarmo.soundseeker.product.persistence.impl.ProductDaoH2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

class ProductServiceTest {
    static final Logger LOGGER = LogManager.getLogger();
    ProductService productService;

    @BeforeEach
    void setUp() {
        try (Connection connection = DBConnection.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("RUNSCRIPT FROM 'classpath:init.sql'");
            }
            productService = new ProductService(new ProductDaoH2());
            LOGGER.info("✔ Database reset successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
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
        LOGGER.info("✔ The placeholder product was saved successfully and the data matches the expected values.");
    }

    @Test
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
        LOGGER.info("✔ The product was found successfully and the data matches the expected values.");
    }

    @Test
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
        LOGGER.info(table.render());
        LOGGER.info("✔ The product was updated successfully and the data matches the expected values.");
    }

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

    @Test
    void givenAnExistingProductId_whenDeleted_thenItShouldBeRemovedFromTheDatabase() {
        productService.delete(1L);

        Product product = productService.findById(1L);

        assertThat(product).isNull();
        LOGGER.info("✔ The product was deleted successfully and it is no longer in the database.");
    }
}