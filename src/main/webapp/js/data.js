/**
 * Datos estáticos de la aplicación SoundSeeker
 * En una versión futura, estos datos podrían cargarse desde el servidor
 */

export const brands = [
    { id: 1, name: 'Armstrong' },
    { id: 2, name: 'Bach' },
    { id: 3, name: 'Conn' },
    { id: 4, name: 'Conn-Selmer' },
    { id: 5, name: 'Cort' },
    { id: 6, name: 'Fender' },
    { id: 7, name: 'Gliga' },
    { id: 8, name: 'Hohner' },
    { id: 9, name: 'Jimbao' },
    { id: 10, name: 'Kalani' },
    { id: 11, name: 'Kawai' },
    { id: 12, name: 'Kurzweil' },
    { id: 13, name: 'Leblanc' },
    { id: 14, name: 'LP' },
    { id: 15, name: 'Ludwig' },
    { id: 16, name: 'Mahalo' },
    { id: 17, name: 'Medeli' },
    { id: 18, name: 'Pearl' },
    { id: 19, name: 'Roland' },
    { id: 20, name: 'Steinway & Sons' },
    { id: 21, name: 'Tom Grasso' },
    { id: 22, name: 'Tycoon' },
    { id: 23, name: 'Valencia' },
    { id: 24, name: 'Verona' },
    { id: 25, name: 'Yamaha' },
];

export const categories = [
    {
        id: 1,
        name: 'Guitarras y Cuerdas',
        icon: '/img/guitars.svg',
    },
    {
        id: 2,
        name: 'Acordeones',
        icon: '/img/square-sliders-vertical.svg',
    },
    {
        id: 3,
        name: 'Pianos',
        icon: '/img/piano.svg',
    },
    {
        id: 4,
        name: 'Percusión',
        icon: '/img/triangle-instrument.svg',
    },
    {
        id: 5,
        name: 'Teclados',
        icon: '/img/piano-keyboard.svg',
    },
    {
        id: 6,
        name: 'Baterías',
        icon: '/img/drum.svg',
    },
    {
        id: 7,
        name: 'Vientos',
        icon: '/img/saxophone.svg',
    },
    {
        id: 8,
        name: 'Violines y Violas',
        icon: '/img/violin.svg',
    },
];

/**
 * Encuentra una marca por su ID
 * @param {number} id - ID de la marca
 * @returns {Object|undefined} - Objeto de marca o undefined si no se encuentra
 */
export function findBrandById(id) {
    return brands.find((brand) => brand.id === id);
}

/**
 * Encuentra una marca por su nombre
 * @param {string} name - Nombre de la marca
 * @returns {Object|undefined} - Objeto de marca o undefined si no se encuentra
 */
export function findBrandByName(name) {
    return brands.find((brand) => brand.name === name);
}

/**
 * Encuentra información de categoría por su ID
 * @param {number} id - ID de la categoría
 * @returns {Array} - [icon, name] o [null, null] si no se encuentra
 */
export function findCategoryInfoById(id) {
    const category = categories.find((category) => category.id === parseInt(id));
    return category ? [category.icon, category.name] : [null, null];
}

/**
 * Formatea un valor como moneda
 * @param {number|string} value - Valor a formatear
 * @returns {string} - Valor formateado como moneda
 */
export function formatCurrency(value, options = {}) {
    const { locale = 'es-UY', currency = 'UYU', minDecimals = 2, maxDecimals = 2 } = options;

    return Intl.NumberFormat(locale, {
        style: 'currency',
        currency: currency,
        minimumFractionDigits: minDecimals,
        maximumFractionDigits: maxDecimals,
    }).format(value);
}
