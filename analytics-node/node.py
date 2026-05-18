import os
import uvicorn
from fastapi import FastAPI, Request
from dotenv import load_dotenv

load_dotenv()

app = FastAPI()

@app.post("/new-reservation")
async def recibir_reserva(request: Request):
    try:
        # Capturar el JSON completo que envía Supabase
        payload = await request.json()
        
        print("\n================ ¡WEBHOOK RECIBIDO! ================")
        # Ver la fila que se acaba de insertar
        nueva_reserva = payload.get("record", {})
        print("Datos de la reserva entrante:")
        print(nueva_reserva)
        print("====================================================\n")
        
        # Respuesta a Supabase para que no reintente
        return {
            "status": "success", 
            "message": "Webhook recibido correctamente en el nodo Python",
            "id_detectado": nueva_reserva.get("reservation_id")
        }
        
    except Exception as e:
        print(f"Error al procesar el webhook: {e}")
        return {"status": "error", "message": str(e)}

# Arranque directo
if __name__ == "__main__":
    # Arranca Uvicorn en el puerto 8000
    uvicorn.run("node:app", host="0.0.0.0", port=8000, reload=True)