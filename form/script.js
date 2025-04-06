// Traducciones para cada idioma
const translations = {
  es: {
    headline: "Entérate de nuestras novedades.",
    description: "Déjanos tus datos para recibir información exclusiva de nuestra fase beta, el lanzamiento de la app y mucho más.",
    endline: "¡Únete a nuestra comunidad y no te pierdas nada!",
    nameLabel: "Nombre",
    emailLabel: "Correo electrónico",
    submit: "Enviar",
    successMessage: "Los datos se han enviado correctamente, gracias por confiar en nosotros 🫶"
  },
  gl: {
    headline: "Infórmate das nosas novidades.",
    description: "Déixanos os teus datos para recibir información exclusiva sobre a nosa fase beta, o lanzamento da app e moito máis.",
    endline: "Únete á nosa comunidade e non perdas nada!",
    nameLabel: "Nome",
    emailLabel: "Correo electrónico",
    submit: "Enviar",
    successMessage: "Os datos enviáronse correctamente, grazas por confiar en nós 🫶"
  },
  pt: {
    headline: "Fique por dentro das nossas novidades.",
    description: "Deixe seus dados para receber informações exclusivas sobre nossa fase beta, o lançamento do app e muito mais.",
    endline: "Junte-se à nossa comunidade e não perca nada!",
    nameLabel: "Nome",
    emailLabel: "E-mail",
    submit: "Enviar",
    successMessage: "Os dados foram enviados com sucesso, obrigado por confiar em nós 🫶"
  },
  en: {
    headline: "Stay up to date with our news.",
    description: "Leave us your details to receive exclusive information about our beta phase, the app launch, and much more.",
    endline: "Join our community and don't miss a thing!",
    nameLabel: "Name",
    emailLabel: "Email",
    submit: "Submit",
    successMessage: "Data sent successfully, thank you for trusting us 🫶"
  }
};

let currentLang = "es";
// Función para actualizar el idioma en la página
function updateLanguage(lang) {
  // Verifica si existe la traducción para el idioma seleccionado
  if (translations[lang]) {
    currentLang = lang;
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
  
  // Firebase Realtime Database URL
  const firebaseDBUrl = 'https://greendotsform-default-rtdb.europe-west1.firebasedatabase.app/user.json';

  // Data to be sent to Firebase
  const userData = {
    name: name,
    email: email
  };

  // Send data to Firebase Realtime Database
  fetch(firebaseDBUrl, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(userData)
  })
  .then(response => {
    if (response.ok) {
      console.log('Data sent to Firebase successfully!');

      // Crear o seleccionar el contenedor del mensaje
      let messageElement = document.getElementById('successMessage');
      if (!messageElement) {
        messageElement = document.createElement('div');
        messageElement.id = 'successMessage';
        // Agregar el mensaje al final del contenedor del formulario
        document.querySelector('.form-container').appendChild(messageElement);
      }
      
      // Establecer el texto y activar la animación con la traducción del mensaje
      messageElement.textContent = translations[currentLang].successMessage;
      messageElement.classList.remove('hidden');  // En caso de usar una clase oculta
      messageElement.classList.add('message-animation');

      // Opcional: quitar el elemento o reiniciar la animación después de 4 segundos
      setTimeout(() => {
        messageElement.classList.remove('message-animation');
        messageElement.classList.add('hidden');
      }, 4000);   
    } else {
      console.error('Error sending data to Firebase:', response.status);
    }
  })
  .catch(error => {
    console.error('Error sending data to Firebase:', error);
  });
});

function testSuccessMessage() {
  // Selecciona o crea el contenedor del mensaje
  let messageElement = document.getElementById('successMessage');
  if (!messageElement) {
    messageElement = document.createElement('div');
    messageElement.id = 'successMessage';
    // Agregar el mensaje al final del contenedor del formulario
    document.querySelector('.form-container').appendChild(messageElement);
  }
  
  // Establece el texto y activa la animación usando la traducción
  messageElement.textContent = translations[currentLang].successMessage;
  messageElement.classList.remove('hidden'); // Remueve la clase oculta si existe
  messageElement.classList.add('message-animation');
  
  // Después de 4 segundos, quita la animación y oculta el mensaje
  setTimeout(() => {
    messageElement.classList.remove('message-animation');
    messageElement.classList.add('hidden');
  }, 4000);
}