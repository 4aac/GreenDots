// Traducciones para cada idioma
const translations = {
  es: {
    headline: "Entérate de nuestras novedades.",
    description: "Déjanos tus datos para recibir información exclusiva de nuestra fase beta, el lanzamiento de la app y mucho más.",
    endline: "¡Únete a nuestra comunidad y no te pierdas nada!",
    nameLabel: "Nombre",
    emailLabel: "Correo electrónico",
    submit: "Enviar"
  },
  gl: {
    headline: "Infórmate das nosas novidades.",
    description: "Déixanos os teus datos para recibir información exclusiva sobre a nosa fase beta, o lanzamento da app e moito máis.",
    endline: "Únete á nosa comunidade e non perdas nada!",
    nameLabel: "Nome",
    emailLabel: "Correo electrónico",
    submit: "Enviar"
  },
  pt: {
    headline: "Fique por dentro das nossas novidades.",
    description: "Deixe seus dados para receber informações exclusivas sobre nossa fase beta, o lançamento do app e muito mais.",
    endline: "Junte-se à nossa comunidade e não perca nada!",
    nameLabel: "Nome",
    emailLabel: "E-mail",
    submit: "Enviar"
  },
  en: {
    headline: "Stay up to date with our news.",
    description: "Leave us your details to receive exclusive information about our beta phase, the app launch, and much more.",
    endline: "Join our community and don't miss a thing!",
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
