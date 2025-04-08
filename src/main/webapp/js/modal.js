/**
 * Gestión de modales basada en Pico.css
 */
import { config } from './config.js';

const { ui } = config;
const { openClass, openingClass, closingClass, scrollbarWidthCssVar } = ui.modal;
const { duration } = ui.animation;

// Referencia al modal visible
export let visibleModal = null;

/**
 * Obtiene el ancho de la barra de desplazamiento
 * @returns {number} - Ancho de la barra de desplazamiento
 */
export const getScrollbarWidth = () => {
    return window.innerWidth - document.documentElement.clientWidth;
};

/**
 * Verifica si la barra de desplazamiento es visible
 * @returns {boolean} - Si la barra de desplazamiento es visible
 */
export const isScrollbarVisible = () => {
    return document.body.scrollHeight > screen.height;
};

/**
 * Abre un modal
 * @param {HTMLElement} modal - Modal a abrir
 */
export const openModal = (modal) => {
    const { documentElement: html } = document;
    const scrollbarWidth = getScrollbarWidth();

    if (scrollbarWidth) {
        html.style.setProperty(scrollbarWidthCssVar, `${scrollbarWidth}px`);
    }

    html.classList.add(openClass, openingClass);

    setTimeout(() => {
        visibleModal = modal;
        html.classList.remove(openingClass);
    }, duration);

    modal.showModal();
};

/**
 * Cierra un modal
 * @param {HTMLElement} modal - Modal a cerrar
 * @param {Function} [onClose] - Función a ejecutar después de cerrar
 */
export const closeModal = (modal, onClose) => {
    visibleModal = null;
    const { documentElement: html } = document;

    html.classList.add(closingClass);

    setTimeout(() => {
        html.classList.remove(closingClass, openClass);
        html.style.removeProperty(scrollbarWidthCssVar);
        modal.close();

        if (typeof onClose === 'function') {
            onClose();
        }
    }, duration);
};

/**
 * Alterna la visibilidad de un modal
 * @param {Event} event - Evento que disparó la acción
 * @param {Function} [onClose] - Función a ejecutar después de cerrar
 */
export const toggleModal = (event, onClose) => {
    event.preventDefault();
    const targetId = event.currentTarget.dataset.target;
    const modal = document.getElementById(targetId);

    if (!modal) return;

    if (modal.open) {
        closeModal(modal, onClose);
    } else {
        openModal(modal);
    }
};

// Inicializa los listeners para cerrar modales
export function initModalListeners() {
    // Cerrar con clic fuera del contenido
    document.addEventListener('click', (event) => {
        if (visibleModal === null) return;

        const modalContent = visibleModal.querySelector('article');
        const isClickInside = modalContent.contains(event.target);

        if (!isClickInside) {
            closeModal(visibleModal);
        }
    });

    // Cerrar con tecla Escape
    document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape' && visibleModal) {
            closeModal(visibleModal);
        }
    });
}

// Inicializa los listeners
initModalListeners();
