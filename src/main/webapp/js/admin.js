/**
 * Módulo principal para la gestión de administración de productos
 */
import { config } from './config.js';
import { findBrandById, findBrandByName, findCategoryInfoById, formatCurrency } from './data.js';
import { toggleModal, closeModal } from './modal.js';
import {
    validateInputLength,
    validateInputNumber,
    validateInputSelect,
    validateSelectBrandInput,
    validateInputFile,
    isFormValid,
} from './validation.js';
import {
    getAllProducts as fetchProducts,
    getProductById,
    createProduct,
    updateProduct,
    deleteProduct,
    uploadFile,
} from './api-service.js';

// Inicialización y configuración de event listeners

/**
 * Inicializa la aplicación
 */
function init() {
    // Configurar estado inicial
    formState.available = elements.availableInput.checked;
    formState.isValid.available = true;

    // Configurar event listeners
    setupEventListeners();

    // Cargar productos
    setTimeout(() => {
        loadAndDisplayProducts();
    }, 500);
}

/**
 * Configura los event listeners para los elementos del formulario
 */
function setupEventListeners() {
    // Botones del modal
    elements.registerButton.addEventListener('click', (e) => {
        resetFormState();
        toggleModal(e);
    });

    elements.cancelButton.addEventListener('click', toggleModal);

    // Formulario
    elements.form.addEventListener('submit', handleSubmitProduct);

    // Campos de texto
    elements.nameInput.addEventListener('change', (e) => {
        formState.name = e.target.value;
        formState.isValid.name = validateInputLength(e.target);
    });

    elements.descriptionInput.addEventListener('change', (e) => {
        formState.description = e.target.value;
        formState.isValid.description = validateInputLength(
            e.target,
            config.validation.description.minLength,
            config.validation.description.maxLength
        );
    });

    // Campos numéricos
    elements.priceInput.addEventListener('change', (e) => {
        formState.price = e.target.value;
        formState.isValid.price = validateInputNumber(e.target);
    });

    // Checkbox
    elements.availableInput.addEventListener('change', (e) => {
        formState.available = e.target.checked;
        formState.isValid.available = true;
    });

    // Selects
    elements.selectBrandInput.addEventListener('change', (e) => {
        const value = parseInt(e.target.value);

        if (value > 0) {
            const brand = findBrandById(value);
            formState.brand = brand ? brand.name : '';
        }

        formState.isValid.brand = validateSelectBrandInput(
            e.target,
            elements.newBrandLabel,
            elements.newBrandInput
        );
    });

    elements.newBrandInput.addEventListener('change', (e) => {
        formState.brand = e.target.value;
        formState.isValid.brand = validateInputLength(e.target);
    });

    elements.categoryInput.addEventListener('change', (e) => {
        formState.categoryId = e.target.value;
        formState.isValid.categoryId = validateInputSelect(e.target);
    });

    // Archivo
    elements.thumbnailInput.addEventListener('change', async (e) => {
        const isValid = await validateInputFile(e.target);
        formState.isValid.thumbnail = isValid;

        if (isValid && e.target.files[0]) {
            await handleUploadThumbnail(e.target.files[0]);
        }
    });
}

// Referencias a elementos del DOM
const elements = {
    tableBody: document.getElementById('products-table-body'),
    form: document.getElementById('product-form'),
    formTitle: document.getElementById('product-form-title'),
    formSubtitle: document.getElementById('product-form-subtitle'),
    idInput: document.getElementById('product-id'),
    nameInput: document.getElementById('product-name'),
    descriptionInput: document.getElementById('product-description'),
    selectBrandInput: document.getElementById('product-select-brand'),
    newBrandLabel: document.querySelector('label[for="product-new-brand"]'),
    newBrandInput: document.getElementById('product-new-brand'),
    priceInput: document.getElementById('product-price'),
    availableInput: document.getElementById('product-available'),
    thumbnailInput: document.getElementById('product-thumbnail'),
    categoryInput: document.getElementById('product-category'),
    submitButton: document.querySelector('button[type="submit"]'),
    registerButton: document.getElementById('register-button'),
    cancelButton: document.getElementById('product-form-cancel'),
    progressBar: document.querySelector('progress'),
    modal: document.getElementById('register-dialog'),
};

// Estado del formulario
const formState = {
    name: undefined,
    description: undefined,
    brand: undefined,
    price: undefined,
    available: undefined,
    thumbnail: undefined,
    categoryId: undefined,
    isValid: {
        name: false,
        description: false,
        brand: false,
        price: false,
        available: false,
        thumbnail: false,
        categoryId: false,
    },
};

// Inicializar cuando el DOM esté cargado
document.addEventListener('DOMContentLoaded', init);

/**
 * Renderiza la tabla de productos
 * @param {Array} products - Lista de productos
 */
function renderProductsTable(products) {
    elements.tableBody.innerHTML = '';

    products.forEach((product) => {
        const [categoryIcon, categoryName] = findCategoryInfoById(product.categoryId);
        const tr = document.createElement('tr');

        tr.innerHTML = `
            <td class="center">${product.id}</td>
            <td class="center">
                <img
                    src="${config.server.url + product.thumbnail}"
                    alt="${product.name}"
                    width="100"
                    height="100" />
            </td>
            <td>${product.name}</td>
            <td>${product.description}</td>
            <td class="center">${product.brand}</td>
            <td class="center">${formatCurrency(product.price.toFixed(2))}</td>
            <td class="center">
                <span data-tooltip="${product.available ? 'Disponible' : 'No disponible'}">
                    <img
                        src="${product.available ? 'img/circle-check.svg' : 'img/circle-xmark.svg'}"
                        alt="${product.available ? 'Disponible' : 'No disponible'}"
                        width="30"
                        height="30" />
                </span>
            </td>
            <td class="center">
                <span data-tooltip="${categoryName}">
                    <img
                        src="${categoryIcon}"
                        alt="${categoryName}"
                        width="30"
                        height="30" />
                </span>
            </td>
            <td class="actions center">
                <button
                    type="button"
                    class="edit secondary"
                    data-placement="left"
                    data-tooltip="Editar"
                    data-id="${product.id}">
                    <img
                        src="img/pen-to-square-solid.svg"
                        alt="Editar"
                        width="20"
                        height="20" />
                </button>
                <button
                    type="button"
                    class="delete"
                    data-placement="left"
                    data-tooltip="Eliminar"
                    data-id="${product.id}">
                    <img
                        src="img/trash-solid.svg"
                        alt="Eliminar"
                        width="20"
                        height="20" />
                </button>
            </td>
        `;

        elements.tableBody.appendChild(tr);

        // Agregar event listeners a los botones
        tr.querySelector('.edit').addEventListener('click', () => handleEditProduct(product.id));

        tr.querySelector('.delete').addEventListener('click', () =>
            handleDeleteProduct(product.id)
        );
    });
}

/**
 * Carga los productos y los muestra en la tabla
 */
async function loadAndDisplayProducts() {
    try {
        elements.progressBar.removeAttribute('hidden');
        const products = await fetchProducts();
        renderProductsTable(products);
    } catch (error) {
        alert('Error al cargar los productos. Por favor, intenta de nuevo.');
    } finally {
        elements.progressBar.setAttribute('hidden', '');
    }
}

/**
 * Maneja la edición de un producto
 * @param {number} id - ID del producto a editar
 */
async function handleEditProduct(id) {
    try {
        // Obtener datos del producto
        const product = await getProductById(id);

        // Configurar UI para edición
        elements.formTitle.textContent = '¡Actualiza ese producto viejo!';
        elements.formSubtitle.textContent = 'Porque es necesario estar al día.';
        elements.submitButton.textContent = '¡Actualizar ya!';
        elements.cancelButton.textContent = 'Me arrepentí y no quiero continuar.';

        // Llenar el formulario con los datos del producto
        elements.idInput.value = product.id;
        elements.nameInput.value = product.name;
        elements.descriptionInput.value = product.description;
        elements.priceInput.value = product.price;
        elements.availableInput.checked = product.available;
        elements.categoryInput.value = product.categoryId;

        // Manejar la marca (existente o nueva)
        const brandOption = findBrandByName(product.brand);
        if (brandOption) {
            elements.selectBrandInput.value = brandOption.id;
            elements.newBrandLabel.setAttribute('hidden', '');
            elements.newBrandInput.setAttribute('hidden', '');
        } else {
            elements.selectBrandInput.value = 0;
            elements.newBrandLabel.removeAttribute('hidden');
            elements.newBrandInput.removeAttribute('hidden');
            elements.newBrandInput.value = product.brand;
        }

        // Actualizar el estado del formulario
        formState.name = product.name;
        formState.description = product.description;
        formState.brand = product.brand;
        formState.price = product.price;
        formState.available = product.available;
        formState.thumbnail = product.thumbnail;
        formState.categoryId = product.categoryId;

        // Marcar todos los campos como válidos
        Object.keys(formState.isValid).forEach((key) => {
            formState.isValid[key] = true;
        });

        // Abrir el modal
        toggleModal({
            preventDefault: () => {},
            currentTarget: { dataset: { target: 'register-dialog' } },
        });
    } catch (error) {
        alert(`Error al cargar el producto: ${error.message}`);
    } finally {
        elements.progressBar.setAttribute('hidden', '');
    }
}

/**
 * Maneja la eliminación de un producto
 * @param {number} id - ID del producto a eliminar
 */
async function handleDeleteProduct(id) {
    if (
        !confirm(
            '¿Estás seguro de que deseas eliminar este producto? Esta acción no se puede deshacer.'
        )
    ) {
        return;
    }

    try {
        elements.progressBar.removeAttribute('hidden');
        await deleteProduct(id);
        await loadAndDisplayProducts();
    } catch (error) {
        alert(`Error al eliminar el producto: ${error.message}`);
    } finally {
        elements.progressBar.setAttribute('hidden', '');
    }
}

/**
 * Maneja la subida de una imagen
 * @param {File} file - Archivo a subir
 */
async function handleUploadThumbnail(file) {
    try {
        formState.thumbnail = await uploadFile(file);
    } catch (error) {
        alert(`Error al subir la imagen: ${error.message}`);
        formState.thumbnail = undefined;
        formState.isValid.thumbnail = false;
    }
}

/**
 * Resetea el estado del formulario
 */
/**
 * Resetea el estado del formulario
 */
function resetFormState() {
    // Resetear estado
    Object.keys(formState).forEach((key) => {
        if (key !== 'isValid') {
            formState[key] = undefined;
        }
    });

    // Resetear validaciones
    Object.keys(formState.isValid).forEach((key) => {
        formState.isValid[key] = false;
    });

    // Resetear formulario
    elements.form.reset();

    // Limpiar ID para asegurar que se trata como nuevo producto
    elements.idInput.value = '';

    // Ocultar mensajes de error
    const invalidHelpers = document.querySelectorAll('[id$="-invalid-helper"]');
    invalidHelpers.forEach((helper) => {
        helper.setAttribute('hidden', '');
        const inputId = helper.id.replace('-invalid-helper', '');
        const input = document.getElementById(inputId);
        if (input) {
            input.removeAttribute('aria-invalid');
        }
    });

    // Configurar UI para agregar nuevo producto
    elements.formTitle.textContent = '¡Agrega un instrumento!';
    elements.formSubtitle.textContent = 'Ingresa todos los datos ¡y ya está!';
    elements.submitButton.textContent = '¡Guardar en el catálogo!';
    elements.cancelButton.textContent = '¡Ya no quiero agregar un instrumento!';

    // Ocultar campo de nueva marca
    elements.newBrandLabel.setAttribute('hidden', '');
    elements.newBrandInput.setAttribute('hidden', '');
    elements.selectBrandInput.removeAttribute('disabled');
}

/**
 * Maneja el envío del formulario
 * @param {Event} event - Evento de envío
 */
async function handleSubmitProduct(event) {
    event.preventDefault();

    if (!isFormValid(formState.isValid)) {
        alert('Por favor, completa todos los campos correctamente.');
        return;
    }

    const productData = {
        name: formState.name,
        description: formState.description,
        brand: formState.brand,
        price: parseFloat(formState.price),
        available: elements.availableInput.checked,
        thumbnail: formState.thumbnail,
        categoryId: parseInt(formState.categoryId),
    };

    // UI feedback - botón en estado de carga
    elements.submitButton.setAttribute('aria-busy', 'true');
    elements.submitButton.textContent = 'Guardando...';

    try {
        const id = parseInt(elements.idInput.value);
        let result;

        if (id) {
            // Actualizar producto existente
            result = await updateProduct(id, productData);
        } else {
            // Crear nuevo producto
            result = await createProduct(productData);
        }

        // Éxito
        elements.submitButton.textContent = '¡Guardado con éxito!';
        setTimeout(() => {
            closeModal(elements.modal, resetFormState);
            loadAndDisplayProducts();
        }, 1000);
    } catch (error) {
        // Error
        elements.submitButton.textContent = 'Error al guardar';
        alert(`Error: ${error.message || 'No se pudo guardar el producto'}`);
    } finally {
        // Restablecer estado del botón
        setTimeout(() => {
            elements.submitButton.removeAttribute('aria-busy');
        }, 1000);
    }
}
