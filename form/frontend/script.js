// Traducciones para cada idioma
const translations = {
  es: {
    headline: "¿Quieres ser el primero en enterarte de las últimas novedades de GreenDots?",
    description: "Déjanos tu nombre y correo para recibir información exclusiva sobre nuestras fases beta, el lanzamiento de la app y mucho más.",
    endline: "¡Únete a nuestra comunidad y no te pierdas ninguna novedad!",
    nameLabel: "Nombre",
    emailLabel: "Correo electrónico",
    submit: "Enviar"
  },
  gl: {
    headline: "Queres ser o primeiro en saber as últimas novidades de GreenDots?",
    description: "Deixanos o teu nome e correo para recibir información exclusiva sobre as nosas fases beta, o lanzamento da aplicación e moito máis.",
    endline: "Únete á nosa comunidade e non perdas ningunha novidade!",
    nameLabel: "Nome",
    emailLabel: "Correo electrónico",
    submit: "Enviar"
  },
  pt: {
    headline: "Quer ser o primeiro a saber das últimas novidades do GreenDots?",
    description: "Deixe-nos o seu nome e e-mail para receber informações exclusivas sobre as nossas fases beta, o lançamento da aplicação e muito mais.",
    endline: "Junte-se à nossa comunidade e não perca nenhuma novidade!",
    nameLabel: "Nome",
    emailLabel: "E-mail",
    submit: "Enviar"
  },
  en: {
    headline: "Do you want to be the first to hear the latest news about GreenDots?",
    description: "Leave us your name and email to receive exclusive information about our beta phases, the app launch and much more.",
    endline: "Join our community and don't miss any news!",
    nameLabel: "Name",
    emailLabel: "Email",
    submit: "Submit"
  }
};

// Función para actualizar el idioma en la página
function updateLanguage(lang) {
  // Verifica si existe la traducción para el idioma seleccionado
  if (translations[lang]) {
    document.getElementById("headline").innerHTML = `<strong>${translations[lang].headline}</strong>`;
    document.getElementById("description").textContent = translations[lang].description;
    document.getElementById("endline").textContent = translations[lang].endline;
    document.getElementById("labelName").textContent = translations[lang].nameLabel;
    document.getElementById("labelEmail").textContent = translations[lang].emailLabel;
    document.getElementById("submitButton").textContent = translations[lang].submit;
  }
}

// Detectar el idioma predeterminado del navegador
const browserLang = navigator.language.slice(0, 2);
if (translations[browserLang]) {
  updateLanguage(browserLang);
} else {
  // Si el idioma del navegador no está disponible, se puede establecer uno por defecto, por ejemplo "es"
  updateLanguage("es");
}

// Asignar eventos a los botones del selector de idioma
document.querySelectorAll("#language-selector button").forEach(button => {
  button.addEventListener("click", function() {
    const selectedLang = this.getAttribute("data-lang");
    updateLanguage(selectedLang);
  });
});

// Evento para el envío del formulario
document.getElementById('contactForm').addEventListener('submit', function(event) {
  event.preventDefault();

  // Obtener los valores de los campos
  const name = document.getElementById('name').value;
  const email = document.getElementById('email').value;
  
  // Crear el objeto con los datos a enviar usando las claves que espera el backend
  const data = { nombre: name, correo: email };

  // Enviar datos al endpoint /usuarios en el puerto 4444
  fetch('http://127.0.0.1:4444/usuarios', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  })
  .then(response => {
    if(response.ok) {
      alert('Datos enviados correctamente');
    } else {
      alert('Error al enviar los datos');
    }
  })
  .catch(error => {
    console.error('Error:', error);
    alert('Error al enviar los datos');
  });
});
