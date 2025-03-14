# 🌍 Green Dots

## 🚀 Impact-thon ETSE - USC

Welcome to **Green Dots**, a mobile application designed to empower users to explore businesses that contribute to a more sustainable and responsible world. As society moves forward towards eco-friendly and ethical practices, Sustainable Maps provides a platform to discover, review, and evaluate businesses based on their adherence to Sustainable Development Goals (SDGs).

> [!CAUTION]
> This application is a demo and is not intended for production use.
> Please note that it is not safe for storing confidential or sensitive data, such as real user passwords.
> Use this application for testing and development purposes only. Thank you for your understanding!

---

## 📌 How Does It Work?

1. **Sign in** – Create an account, set up your profile with your favorite photo, and add a short description about yourself.
2. **Explore & Search** – Choose an area to explore and search for businesses that interest you.
3. **Evaluate Businesses** – Check sustainability statistics related to the SDGs for each business.
4. **Share Your Thoughts** – Leave reviews and contribute valuable feedback to help others make informed choices.
5. **Enjoy Dynamic Navigation** – Experience seamless transitions and an intuitive interface.
6. **Customize Your Experience** – Switch between **day mode** and **night mode** for better usability.

---

## 🏗 Architecture

Sustainable Maps is built with a robust and modern tech stack:

- **Backend:** Server developed in Java using the Spring Boot framework, which allows building a robust REST API.
- **Database:** PostgreSQL for efficient data management, in a lightweight Docker container.
- **Frontend:** Kotlin for a smooth and responsive mobile application experience.

### 💡 Architecture Advantages

Separating application logic into modular components facilitates future development, allowing for the creation of desktop versions, apps for the Apple ecosystem, or even a website. This modularity not only improves code maintainability but also opens the door to future integrations and expansions.

---

## ⚙️ Deployment

To run the application locally, we will follow these steps:

```bash
#Clone the repository
git clone https://github.com/martindios/GreenDots
#Start Docker with the database
cd GreenDots/database/Docker
docker compose up -d
#Build and run the Java server
cd ../Server/
./gradlew build
./gradlew bootRun
```

---

## 🔥 Key Features

✅ **User Profiles** – Personalize your account with a profile picture and description.  
✅ **Location-Based Search** – Find and explore businesses with ease.  
✅ **Sustainability Insights** – Gain visibility into how businesses align with SDGs.  
✅ **Smooth Navigation** – Enjoy beautiful animations and transitions.  
✅ **Light/Dark Mode** – Adjust the theme to match your preferences.  
✅ **Community Reviews** – Read and leave opinions to help others make better choices.  

---

## 🌍 Open Source and Accessibility

As an open source application, the API can be released or shared, allowing other projects to access our application's data without restrictions. This can be especially beneficial for university studies or companies interested in acquiring collected information, fostering collaboration and knowledge sharing.

---

## 📜 License

Sustainable Maps is released under the **Apache License 2.0**. This means you are free to use, modify, and distribute the project as long as you adhere to the license terms. For more details, check the `LICENSE` section of this repository.

---

## 💡 Get Involved!

We welcome contributions from developers, designers, and sustainability enthusiasts! Feel free to fork the repository, submit issues, or create pull requests.

### 🌱 Together, let’s build a greener future! 🌍
