from flask import Flask, request, jsonify
from flask_cors import CORS
import psycopg2
import os

app = Flask(__name__)
CORS(app)

# Configuración de la conexión a PostgreSQL
DB_HOST = os.getenv('DB_HOST', 'db')
DB_NAME = os.getenv('DB_NAME', 'formgreendotsdb')
DB_USER = os.getenv('DB_USER', 'testuser')
DB_PASSWORD = os.getenv('DB_PASSWORD', 'testpassword')

def connect_db():
    return psycopg2.connect(
        host=DB_HOST,
        database=DB_NAME,
        user=DB_USER,
        password=DB_PASSWORD
    )

@app.route('/usuarios', methods=['POST'])
def create_usuario():
    data = request.get_json()
    nombre = data.get('nombre')
    correo = data.get('correo')
    
    if not nombre or not correo:
        return jsonify({'error': 'El nombre y el correo son requeridos'}), 400

    try:
        conn = connect_db()
        cursor = conn.cursor()
        cursor.execute(
            "INSERT INTO usuarios (nombre, correo) VALUES (%s, %s)",
            (nombre, correo)
        )
        conn.commit()
        cursor.close()
        conn.close()
        return jsonify({'message': 'Usuario creado correctamente'}), 201
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=4444)

