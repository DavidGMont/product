/**
 * Configuración centralizada de la aplicación SoundSeeker
 */
export const config = {
    // Configuración del servidor
    server: {
        host: 'http://localhost',
        port: 8080,
        get url() {
            return `${this.host}:${this.port}`;
        },
        get apiUrl() {
            return `${this.url}/api/v1`;
        },
    },

    // Límites para validaciones
    validation: {
        text: {
            minLength: 2,
            maxLength: 60,
        },
        description: {
            minLength: 4,
            maxLength: 1000,
        },
        price: {
            min: 1,
        },
        file: {
            maxSize: 1024 * 1024, // 1MB
            validMimeTypes: ['image/jpeg', 'image/png', 'image/gif', 'image/webp'],
            validExtensions: /\.(jpg|jpeg|gif|png|webp)$/i,
            // Firmas de archivos válidos en hexadecimal
            validSignatures: {
                ffd8ffe0: 'JPEG',
                ffd8ffe1: 'JPEG',
                ffd8ffe2: 'JPEG',
                ffd8ffe3: 'JPEG',
                ffd8ffe8: 'JPEG',
                '89504e47': 'PNG',
                47494638: 'GIF',
                52494646: 'WEBP',
            },
        },
    },

    // Configuración de la interfaz
    ui: {
        animation: {
            duration: 400, // ms
        },
        modal: {
            openClass: 'modal-is-open',
            openingClass: 'modal-is-opening',
            closingClass: 'modal-is-closing',
            scrollbarWidthCssVar: '--pico-scrollbar-width',
        },
        currency: {
            locale: 'es-UY',
            currency: 'UYU',
            minDecimals: 2,
            maxDecimals: 2,
        },
    },
};
