/**
 * Módulo de validación centralizado para la aplicación SoundSeeker
 */
import { config } from './config.js';

/**
 * Muestra u oculta un mensaje de error y marca el input como válido o inválido
 * @param {HTMLElement} input - Elemento input a validar
 * @param {boolean} isValid - Si el input es válido o no
 * @returns {boolean} - Devuelve el valor de isValid para encadenamiento
 */
function updateInputValidity(input, isValid) {
    const helper = document.querySelector(`#${input.id}-invalid-helper`);

    if (!isValid) {
        input.setAttribute('aria-invalid', 'true');
        helper?.removeAttribute('hidden');
    } else {
        input.setAttribute('aria-invalid', 'false');
        helper?.setAttribute('hidden', '');
    }

    return isValid;
}

/**
 * Valida la longitud de un texto
 * @param {HTMLElement} input - Elemento input a validar
 * @param {number} minLength - Longitud mínima
 * @param {number} maxLength - Longitud máxima
 * @returns {boolean} - Si el input es válido
 */
export function validateInputLength(
    input,
    minLength = config.validation.text.minLength,
    maxLength = config.validation.text.maxLength
) {
    const value = input.value.trim();
    const isValid = value.length >= minLength && value.length <= maxLength;

    return updateInputValidity(input, isValid);
}

/**
 * Valida un campo numérico
 * @param {HTMLElement} input - Elemento input a validar
 * @param {number} minValue - Valor mínimo aceptado
 * @returns {boolean} - Si el input es válido
 */
export function validateInputNumber(input, minValue = config.validation.price.min) {
    const value = parseFloat(input.value);
    const isValid = !isNaN(value) && value > minValue;

    return updateInputValidity(input, isValid);
}

/**
 * Valida un campo de selección
 * @param {HTMLElement} input - Elemento select a validar
 * @returns {boolean} - Si el input es válido
 */
export function validateInputSelect(input) {
    const value = input.value;
    const isValid = value !== '' && value !== '0';

    return updateInputValidity(input, isValid);
}

/**
 * Valida el campo de marca y gestiona la visibilidad del campo de nueva marca
 * @param {HTMLElement} selectInput - Elemento select de marcas
 * @param {HTMLElement} newBrandLabel - Etiqueta del campo de nueva marca
 * @param {HTMLElement} newBrandInput - Campo de nueva marca
 * @returns {boolean} - Si el input es válido
 */
export function validateSelectBrandInput(selectInput, newBrandLabel, newBrandInput) {
    const selectedOption = selectInput.options[selectInput.selectedIndex];
    const isNewBrand = selectedOption.value === '0';

    if (isNewBrand) {
        selectInput.setAttribute('disabled', 'true');
        selectInput.removeAttribute('aria-invalid');
        newBrandLabel.removeAttribute('hidden');
        newBrandInput.removeAttribute('hidden');
        return false;
    } else {
        newBrandLabel.setAttribute('hidden', '');
        newBrandInput.setAttribute('hidden', '');
        return updateInputValidity(selectInput, true);
    }
}

/**
 * Valida un archivo
 * @param {HTMLElement} input - Elemento input file a validar
 * @returns {Promise<boolean>} - Promesa que resuelve a si el input es válido
 */
export function validateInputFile(input) {
    return new Promise((resolve) => {
        const file = input.files[0];
        const fileConfig = config.validation.file;

        // Validar si existe un archivo y su tamaño
        if (!file || file.size > fileConfig.maxSize) {
            return resolve(updateInputValidity(input, false));
        }

        // Validar el tipo MIME y extensión
        if (
            !fileConfig.validMimeTypes.includes(file.type) ||
            !file.name.match(fileConfig.validExtensions)
        ) {
            return resolve(updateInputValidity(input, false));
        }

        // Validar la firma del archivo
        const reader = new FileReader();
        reader.onload = function (e) {
            const uint8Array = new Uint8Array(e.target.result).subarray(0, 4);
            let header = '';
            for (let i = 0; i < uint8Array.length; i++) {
                header += uint8Array[i].toString(16);
            }

            const isValid = Object.keys(fileConfig.validSignatures).includes(header);
            resolve(updateInputValidity(input, isValid));
        };

        reader.readAsArrayBuffer(file);
    });
}

/**
 * Verifica que un conjunto de campos sean válidos
 * @param {Object} validationState - Estado de validación con campos booleanos
 * @returns {boolean} - Si todos los campos son válidos
 */
export function isFormValid(validationState) {
    return Object.values(validationState).every((isValid) => isValid);
}
