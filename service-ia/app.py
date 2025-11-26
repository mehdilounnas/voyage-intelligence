from flask import Flask, request, jsonify
from flask_cors import CORS
import requests
import os
from datetime import datetime
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

app = Flask(__name__)

# Configure CORS properly
CORS(app, resources={
    r"/api/*": {
        "origins": ["http://localhost:8080"],
        "methods": ["GET", "POST", "OPTIONS"],
        "allow_headers": ["Content-Type", "Authorization"],
        "supports_credentials": True
    }
})

# Configuration des API - Load from environment variables
OPENWEATHER_API_KEY = os.getenv('OPENWEATHER_API_KEY')
HUGGINGFACE_API_KEY = os.getenv('HUGGINGFACE_API_KEY')

# URL des API
OPENWEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/weather"
HUGGINGFACE_API_URL = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.2"


def get_weather(city, country=None):
    """
    Récupère les informations météo pour une ville donnée
    """
    try:
        location = f"{city},{country}" if country else city
        params = {
            'q': location,
            'appid': OPENWEATHER_API_KEY,
            'units': 'metric',
            'lang': 'fr'
        }
        response = requests.get(OPENWEATHER_BASE_URL, params=params, timeout=10)
        response.raise_for_status()
        return response.json()
    except Exception as e:
        print(f"Erreur météo: {str(e)}")
        return None


def call_llm_api(prompt):
    """
    Appelle l'API Hugging Face pour générer du texte
    """
    try:
        if not HUGGINGFACE_API_KEY:
            print("Warning: HUGGINGFACE_API_KEY not configured")
            return None
            
        headers = {"Authorization": f"Bearer {HUGGINGFACE_API_KEY}"}
        payload = {
            "inputs": prompt,
            "parameters": {
                "max_new_tokens": 500,
                "temperature": 0.7,
                "top_p": 0.95,
                "return_full_text": False
            }
        }
        response = requests.post(HUGGINGFACE_API_URL, headers=headers, json=payload, timeout=30)
        
        if response.status_code == 200:
            result = response.json()
            if isinstance(result, list) and len(result) > 0:
                return result[0].get('generated_text', '')
        return None
    except Exception as e:
        print(f"Erreur LLM: {str(e)}")
        return None


def generate_fallback_recommendation(destination_data, weather_data):
    """
    Génère une recommandation de secours si l'API LLM échoue
    """
    destination_name = destination_data.get('name', 'cette destination')
    country = destination_data.get('country', '')
    category = destination_data.get('category', 'tourisme')
    
    recommendation = f"Découvrez {destination_name}"
    if country:
        recommendation += f" en {country}"
    recommendation += f", une destination parfaite pour le {category}. "
    
    if weather_data:
        temp = weather_data.get('main', {}).get('temp', 0)
        weather_desc = weather_data.get('weather', [{}])[0].get('description', 'beau temps')
        recommendation += f"Actuellement, il fait {temp}°C avec {weather_desc}. "
        
        if temp > 25:
            recommendation += "C'est le moment idéal pour profiter du plein air ! "
        elif temp < 10:
            recommendation += "Prévoyez des vêtements chauds pour votre visite. "
    
    recommendation += "Une expérience inoubliable vous attend !"
    return recommendation


@app.route('/health', methods=['GET'])
def health_check():
    """
    Endpoint de santé pour vérifier que le service fonctionne
    """
    return jsonify({
        'status': 'healthy',
        'service': 'Service IA Tourism',
        'timestamp': datetime.now().isoformat(),
        'openweather_configured': bool(OPENWEATHER_API_KEY),
        'huggingface_configured': bool(HUGGINGFACE_API_KEY)
    }), 200


@app.route('/api/ia/recommend', methods=['POST', 'OPTIONS'])
def recommend_destination():
    """
    Génère une recommandation personnalisée pour une destination
    Entrée JSON: {
        "destination": {...},  # Informations sur la destination
        "userPreferences": "..."  # Préférences de l'utilisateur (optionnel)
    }
    """
    # Handle OPTIONS request for CORS preflight
    if request.method == 'OPTIONS':
        return '', 200
        
    try:
        data = request.get_json()
        
        if not data or 'destination' not in data:
            return jsonify({'error': 'Données de destination manquantes'}), 400
        
        destination = data.get('destination')
        user_preferences = data.get('userPreferences', '')
        
        # Récupération de la météo
        city = destination.get('city', '')
        country = destination.get('country', '')
        weather_data = get_weather(city, country)
        
        # Construction du prompt pour le LLM
        prompt = f"""Tu es un expert en tourisme. Génère une recommandation attractive et personnalisée pour cette destination:

Destination: {destination.get('name', '')}
Ville: {city}
Pays: {country}
Catégorie: {destination.get('category', '')}
Description: {destination.get('description', '')}
"""
        
        if weather_data:
            temp = weather_data.get('main', {}).get('temp', 0)
            weather_desc = weather_data.get('weather', [{}])[0].get('description', '')
            prompt += f"\nMétéo actuelle: {temp}°C, {weather_desc}"
        
        if user_preferences:
            prompt += f"\nPréférences de l'utilisateur: {user_preferences}"
        
        prompt += "\n\nÉcris une recommandation courte (2-3 phrases) qui donne envie de visiter cette destination."
        
        # Appel au LLM
        llm_response = call_llm_api(prompt)
        
        # Si le LLM échoue, utiliser la recommandation de secours
        if not llm_response:
            llm_response = generate_fallback_recommendation(destination, weather_data)
        
        # Construction de la réponse
        response = {
            'destination': destination.get('name', ''),
            'recommendation': llm_response.strip(),
            'weather': None
        }
        
        if weather_data:
            response['weather'] = {
                'temperature': weather_data.get('main', {}).get('temp'),
                'description': weather_data.get('weather', [{}])[0].get('description', ''),
                'humidity': weather_data.get('main', {}).get('humidity'),
                'windSpeed': weather_data.get('wind', {}).get('speed'),
                'icon': weather_data.get('weather', [{}])[0].get('icon', '')
            }
        
        return jsonify(response), 200
        
    except Exception as e:
        print(f"Erreur: {str(e)}")
        return jsonify({'error': f'Erreur lors de la génération de la recommandation: {str(e)}'}), 500


@app.route('/api/ia/seasonal-advice', methods=['POST', 'OPTIONS'])
def seasonal_advice():
    """
    Donne des conseils selon la saison et la météo
    Entrée JSON: {
        "destination": {...},
        "season": "winter" | "spring" | "summer" | "autumn"
    }
    """
    # Handle OPTIONS request for CORS preflight
    if request.method == 'OPTIONS':
        return '', 200
        
    try:
        data = request.get_json()
        
        if not data or 'destination' not in data:
            return jsonify({'error': 'Données manquantes'}), 400
        
        destination = data.get('destination')
        season = data.get('season', 'summer')
        
        # Récupération de la météo
        city = destination.get('city', '')
        country = destination.get('country', '')
        weather_data = get_weather(city, country)
        
        # Prompt pour le LLM
        prompt = f"""En tant qu'expert voyage, donne 3 conseils pratiques pour visiter {destination.get('name', '')} ({city}, {country}) pendant la saison {season}.
"""
        
        if weather_data:
            temp = weather_data.get('main', {}).get('temp', 0)
            prompt += f"\nMétéo actuelle: {temp}°C"
        
        prompt += "\n\nFormat: Liste de 3 conseils courts et pratiques."
        
        # Appel au LLM
        llm_response = call_llm_api(prompt)
        
        # Conseils de secours
        if not llm_response:
            season_tips = {
                'winter': ['Prévoyez des vêtements chauds', 'Vérifiez les horaires réduits des attractions', 'Profitez de tarifs hors-saison avantageux'],
                'spring': ['Climat agréable pour la randonnée', 'Floraison des jardins', 'Moins de touristes'],
                'summer': ['Protection solaire indispensable', 'Réservez à l\'avance', 'Profitez des activités nautiques'],
                'autumn': ['Températures douces', 'Couleurs automnales magnifiques', 'Tarifs attractifs']
            }
            llm_response = '\n'.join([f"• {tip}" for tip in season_tips.get(season, season_tips['summer'])])
        
        response = {
            'destination': destination.get('name', ''),
            'season': season,
            'advice': llm_response.strip(),
            'currentWeather': None
        }
        
        if weather_data:
            response['currentWeather'] = {
                'temperature': weather_data.get('main', {}).get('temp'),
                'description': weather_data.get('weather', [{}])[0].get('description', '')
            }
        
        return jsonify(response), 200
        
    except Exception as e:
        print(f"Erreur: {str(e)}")
        return jsonify({'error': f'Erreur: {str(e)}'}), 500


@app.route('/api/ia/compare', methods=['POST', 'OPTIONS'])
def compare_destinations():
    """
    Compare plusieurs destinations et recommande la meilleure selon les préférences
    Entrée JSON: {
        "destinations": [...],
        "preferences": "..."
    }
    """
    # Handle OPTIONS request for CORS preflight
    if request.method == 'OPTIONS':
        return '', 200
        
    try:
        data = request.get_json()
        
        if not data or 'destinations' not in data:
            return jsonify({'error': 'Liste de destinations manquante'}), 400
        
        destinations = data.get('destinations', [])
        preferences = data.get('preferences', '')
        
        if len(destinations) == 0:
            return jsonify({'error': 'Aucune destination à comparer'}), 400
        
        # Construction de la liste des destinations pour le prompt
        dest_list = '\n'.join([
            f"{i+1}. {d.get('name', '')} ({d.get('country', '')}) - {d.get('category', '')}"
            for i, d in enumerate(destinations)
        ])
        
        prompt = f"""Compare ces destinations touristiques et recommande la meilleure:

{dest_list}

Préférences de l'utilisateur: {preferences if preferences else 'Aucune préférence spécifique'}

Donne ta recommandation en 2-3 phrases avec la raison principale."""
        
        llm_response = call_llm_api(prompt)
        
        # Réponse de secours
        if not llm_response:
            best_dest = destinations[0]
            llm_response = f"Je recommande {best_dest.get('name', '')} pour son {best_dest.get('category', 'attractivité')}. C'est une destination qui saura vous séduire !"
        
        return jsonify({
            'comparison': llm_response.strip(),
            'destinationsCount': len(destinations)
        }), 200
        
    except Exception as e:
        print(f"Erreur: {str(e)}")
        return jsonify({'error': f'Erreur: {str(e)}'}), 500


if __name__ == '__main__':
    print("🚀 Service IA Tourism démarré sur http://localhost:5000")
    print("📡 Endpoints disponibles:")
    print("  - POST /api/ia/recommend - Recommandation personnalisée")
    print("  - POST /api/ia/seasonal-advice - Conseils saisonniers")
    print("  - POST /api/ia/compare - Comparaison de destinations")
    print("  - GET  /health - Vérification de l'état du service")
    print(f"\n🔑 Configuration:")
    print(f"  - OpenWeather API: {'✅ Configured' if OPENWEATHER_API_KEY else '❌ Missing'}")
    print(f"  - HuggingFace API: {'✅ Configured' if HUGGINGFACE_API_KEY else '❌ Missing'}")
    app.run(debug=True, host='0.0.0.0', port=5000)
