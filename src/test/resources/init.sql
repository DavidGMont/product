DROP TABLE IF EXISTS PRODUCT;

CREATE TABLE PRODUCT
(
    ID          BIGINT AUTO_INCREMENT PRIMARY KEY,
    NAME        VARCHAR(60)    NOT NULL UNIQUE,
    DESCRIPTION VARCHAR(1000)  NOT NULL,
    BRAND       VARCHAR(60)    NOT NULL,
    PRICE       DECIMAL(10, 2) NOT NULL,
    AVAILABLE   BOOLEAN        NOT NULL,
    THUMBNAIL   VARCHAR(255),
    CATEGORY_ID BIGINT         NOT NULL
);

INSERT INTO PRODUCT (NAME, DESCRIPTION, BRAND, PRICE, AVAILABLE, THUMBNAIL, CATEGORY_ID)
VALUES ('Melódica Fire Hohner C9432174 Red-Black (9432/32)',
        'Desde el funk hasta el reggaeton, los ritmos calientes son una parte importante de la experiencia. Los conjuntos funky condicen con los ritmos apasionados, el baile rápido y las bebidas divertidas. Por supuesto, la melódica no puede solo brindar el telón de fondo con sonido perfecto, también tiene que lucir adecuada para la situación. Con la Fire Melódica, hemos dado a nuestra melódica un nuevo aspecto que volará sus cabezas. Teclas negras y rojas y un cuerpo rojo y brillante con una estructura robusta, hermética y el sonido típico de la melódica. ¡Un diseño robusto para un instrumento caliente!',
        'Hohner', 49.99, 1, '/img/lg1jorfm.webp', 7),
       ('Piano Digital Kurzweil M230 Rosewood Con Silla',
        '88 Teclas peso completo con acción graduada de martillo , teclado con sensibilidad ajustable. Presentando una variedad de pianos acústicos y eléctricos , órganos ,guitarra e instrumentos de orquesta. Maneja un panel de control de fácil acceso para las funciones que contiende el piano. Maneja los pedales de sustein, sostenuto y pedal suave. (128 Polifonías - 20 wats speakers).',
        'Kurzweil', 1199.99, 1, '/img/qwvxyop8.webp', 3),
       ('Conga Compacta 11.75 Tycoon Tac-120 BC HC',
        'Las Congas de Tycoon están diseñadas para introducir una dimensión completamente nueva de portabilidad nunca antes vista en nuestra línea de congas. Utilizando la misma construcción de casco Siam Oak con duelas que nuestras congas Master Series, las Congas miden solo 4-3/4\" de altura y están equipados con parches Remo M7 para una máxima durabilidad y retención de afinación. ',
        'Tycoon', 299.99, 1, '/img/r87dlqg6.webp', 4),
       ('Maracas Tycoon Cuero Crudo Round Natural TMS120',
        'Maracas de cuero natural que proporcionan un sonido más completo. Mangos hechos de madera ecológica de Roble Siam Especialmente diseñados de un nuevo material para producir un sonidos nítidos.',
        'Tycoon', 39.99, 1, '/img/4ldumxst.webp', 4),
       ('Güiro Merenguero Pequeño Tycoon',
        'Proporciona la base rítmica de música de merengue o cumbia. Diseñado para dar un sonido durable.', 'Tycoon',
        99.99, 1, '/img/7cqdneee.webp', 4),
       ('Campana Para Timbal LP LP322 Cromado Antiguo',
        'Los poderosos y medios matices producidos por esta campana son tan únicos como el mismo \"El Rey del Timbal\". El Tito Puente Signature Cowbell está hecho en los EE. UU. Y tiene un cáncamo auto-alineador patentado por LP que se ajusta a varillas de 3/8\" a 1/2\" de diámetro.',
        'LP', 99.99, 1, '/img/70h8timj.webp', 4),
       ('Clarinete 17 Llaves SIB Leblanc CL501',
        'El clarinete en madera de granadilla CL501, es hermoso modelo de estudio.', 'Leblanc', 699.99, 1,
        '/img/av5u9qsq.webp', 7),
       ('Trompeta para Estudiante Bach TR500DIR',
        'Los instrumentos Bach Aristocrat son presentados por Conn-Selmer, el mayor fabricante estadounidense de instrumentos de banda. Aristocrat representa una línea premium de instrumentos musicales de calidad a precios razonables para los jóvenes jugadores de hoy en día que se fabrican según nuestras especificaciones exactas. Respaldados por garantías y servicios completos, los instrumentos de Aristocrat son adecuados para jugadores de todas las edades.',
        'Bach', 1499.99, 1, '/img/042uswq9.webp', 7),
       ('Saxofón Alto Conn AS650',
        'Hoy, CG Conn establece el estándar para instrumentos de latón de alta calidad. La larga y exitosa historia de la fabricación ha crecido, impulsada por la pasión y el amor a la música. ¡Toca y lo oirás!',
        'Conn-Selmer', 1699.99, 1, '/img/07sja2qj.webp', 7),
       ('Sintetizador Keytar Roland AX-Edge-B',
        'El AX-Edge es el resultado de décadas de refinamiento que nos han permitido aprender de artistas del mundo entero sobre las mejores cualidades del sintetizador de directo definitivo. El AX-Edge tiene un diseño moderno y estilizado, con un aspecto personalizable mediante Edge Blades intercambiables.',
        'Roland', 999.99, 1, '/img/quk0xaba.webp', 3)
