# 🎫 Turnero — Sistema de turnos multi-empresa

App en **Java + Spring Boot** para emitir números de turno a clientes que llegan a un local,
mostrar el número que se está atendiendo en una pantalla pública y permitir que los
receptores/vendedores avancen al siguiente cliente. Pensada desde el inicio para que un mismo
despliegue sirva a **varias empresas distintas**, cada una con su propio logo, colores, textos
y URL amigable — sin tocar código para dar de alta un negocio nuevo.

## ✨ Qué incluye

- **URL amigable por empresa**: `tudominio.com/mi-negocio`, `tudominio.com/mi-negocio/tomar`,
  `tudominio.com/mi-negocio/panel`.
- **Pantalla pública** que se actualiza sola en tiempo real (Server-Sent Events) cuando el
  receptor llama al siguiente cliente — ideal para poner en un monitor de la sala de espera.
- **Página para tomar turno**: el cliente entra desde su celular (por ejemplo escaneando un QR
  en la puerta) y toca un botón para obtener su número.
- **Panel del receptor**: botón "Llamar siguiente", con campo opcional para indicar
  ventanilla/box/nombre de quien atiende.
- **Personalización total por empresa**, sin recompilar la app: logo (URL de imagen), color
  primario y secundario, prefijo del número (ej. `A-001`, `B-014`), mensaje de bienvenida, y si
  la numeración reinicia cada día o es continua.
- **API REST** para automatizar la creación/edición de empresas y para integrarlo con otros
  sistemas (por ejemplo, para imprimir el ticket en una impresora térmica).
- Base de datos **H2 embebida persistida en archivo** (no requiere instalar nada aparte) — fácil
  de migrar a PostgreSQL el día que haga falta más escala (ver más abajo).

## 📁 Estructura del proyecto

```
turnero/
├── pom.xml
├── Dockerfile
├── src/main/java/com/turnero/
│   ├── TurneroApplication.java
│   ├── model/           Empresa, Turno, EstadoTurno
│   ├── repository/      EmpresaRepository, TurnoRepository
│   ├── service/         ColaService (lógica de la fila), SseService (tiempo real)
│   ├── controller/      VistaController (páginas), EmpresaApiController, TurnoApiController
│   └── config/          DataInitializer (empresa demo), manejadores de error
└── src/main/resources/
    ├── application.properties
    ├── templates/        pantalla.html, tomar.html, panel.html, inicio.html, nueva-empresa.html
    └── static/css/style.css
```

## ▶️ Correrlo en tu máquina

Requisitos: **Java 17+** y **Maven** (o usa el wrapper si lo agregás con `mvn -N wrapper:wrapper`).

```bash
mvn spring-boot:run
```

La app queda en `http://localhost:8080`. Al arrancar por primera vez se crea automáticamente
una empresa de ejemplo:

- Pantalla pública: `http://localhost:8080/demo`
- Tomar turno: `http://localhost:8080/demo/tomar`
- Panel del receptor: `http://localhost:8080/demo/panel`

## 🏢 Cómo agregar una empresa nueva (personalización)

**Opción 1 — Formulario web:** entrá a `/admin/nueva-empresa` y completá nombre, slug (la URL),
logo, colores y mensaje de bienvenida.

**Opción 2 — API REST**, útil si querés automatizar el alta de clientes de tu SaaS:

```bash
curl -X POST http://localhost:8080/api/empresas \
  -H "Content-Type: application/json" \
  -d '{
        "slug": "peluqueria-ana",
        "nombre": "Peluquería Ana",
        "logoUrl": "https://misitio.com/logo.png",
        "colorPrimario": "#e11d48",
        "colorSecundario": "#111827",
        "prefijo": "P",
        "mensajeBienvenida": "¡Bienvenida! Tomá tu turno",
        "reinicioDiario": true
      }'
```

Con eso, automáticamente quedan disponibles `/peluqueria-ana`, `/peluqueria-ana/tomar` y
`/peluqueria-ana/panel`, ya con su propio logo y colores.

Para editar una empresa existente: `PUT /api/empresas/{slug}` con los campos a cambiar.

## 🔌 Referencia rápida de la API

| Método | Endpoint                         | Qué hace                                          |
|--------|-----------------------------------|----------------------------------------------------|
| POST   | `/api/empresas`                  | Crea una empresa nueva                              |
| GET    | `/api/empresas/{slug}`           | Datos de una empresa                                |
| PUT    | `/api/empresas/{slug}`           | Actualiza logo/colores/textos                       |
| POST   | `/api/{slug}/turnos`             | El cliente toma un número nuevo                     |
| POST   | `/api/{slug}/siguiente?ventanilla=Box1` | El receptor llama al siguiente cliente        |
| GET    | `/api/{slug}/actual`             | Turno que se está atendiendo ahora + gente en espera|
| GET    | `/api/{slug}/stream`             | Canal SSE para actualizar la pantalla en vivo       |

## ☁️ Cómo alojarlo GRATIS con URL pública

La opción más simple para Spring Boot hoy en día es **Render.com** (tiene un plan free real, sin
tarjeta de crédito, con Docker). Alternativas al final.

### Opción recomendada: Render.com (gratis)

1. Subí este proyecto a un repositorio de **GitHub** (necesitás el `Dockerfile` incluido).
2. Entrá a [render.com](https://render.com) y creá una cuenta gratuita.
3. **New → Web Service** → conectá tu repositorio de GitHub.
4. Render va a detectar el `Dockerfile` automáticamente (elegí "Docker" como entorno si te lo
   pregunta).
5. Seleccioná el plan **Free**.
6. Deploy. Al terminar te da una URL del tipo `https://turnero-tuapp.onrender.com` — **esa ya es
   tu URL amigable pública**: `https://turnero-tuapp.onrender.com/mi-negocio`.

**Cosas a tener en cuenta del plan free de Render:**
- Son 750 horas/mes gratis, suficiente para un servicio corriendo 24/7.
- Si nadie usa la app durante ~15 minutos, el servicio "duerme" y la próxima visita tarda entre
  40 y 90 segundos en responder mientras arranca de nuevo (normal en planes free). Si esto te
  molesta para la pantalla del local, podés usar un servicio gratuito como **UptimeRobot** para
  hacerle un ping cada 5 minutos y mantenerla despierta.
- La base H2 en archivo vive dentro del contenedor: si Render reinicia o redeploya el servicio,
  se pierde. Para producción real te recomiendo pasar a PostgreSQL — Render también ofrece una
  base Postgres gratuita que podés conectar (ver sección siguiente).

### Migrar a PostgreSQL gratuito en Render (recomendado para producción)

1. En Render: **New → PostgreSQL** (plan free).
2. Copiá la "Internal Database URL" que te da Render.
3. Agregá la dependencia de Postgres al `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.postgresql</groupId>
       <artifactId>postgresql</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```
4. En el servicio web de Render, agregá variables de entorno:
   - `SPRING_DATASOURCE_URL` = la URL que te dio Render (con prefijo `jdbc:postgresql://...`)
   - `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD`
5. Listo — Spring Boot detecta esas variables automáticamente y ya no perdés datos entre
   redeploys.

### Otras opciones gratuitas

- **Railway.app**: da 5 USD de crédito gratis por mes (se renueva mensualmente); muy fácil de
  usar, pero si tu app consume más de eso empieza a cobrar.
- **Oracle Cloud "Always Free"**: te da una VM gratis *para siempre* (no por 12 meses como
  AWS/Azure), con más recursos que Render/Railway. Requiere más trabajo de configuración manual
  (instalar Java, abrir puertos, etc.) pero no tiene el problema de "dormirse" ni límite de
  horas.
- **Fly.io**: tiene capa gratuita, pero suele quedarse corto de memoria para Spring Boot salvo
  que ajustes bien el heap de la JVM.

## 🌐 URL amigable / dominio propio

Ya con Render tenés una URL fácil de recordar y compartir por WhatsApp o en un cartel con QR
(`onrender.com/tu-negocio`). Si más adelante querés tu propio dominio
(`turnos.tuempresa.com`), Render permite conectar un dominio personalizado gratis desde el panel
del servicio (Settings → Custom Domain) — solo hay que apuntar un registro DNS.

## 🎨 Ideas para seguir personalizando a futuro

- Agregar autenticación simple para el panel del receptor (usuario/contraseña por empresa).
- Sonido/voz que anuncie el número al llamar al siguiente cliente.
- Impresión automática del ticket en una impresora térmica conectada al navegador.
- Métricas: tiempo promedio de espera y de atención por empresa.
- Múltiples "ventanillas" simultáneas atendiendo turnos distintos en paralelo.
- Subida de logo como archivo (hoy se usa una URL de imagen para simplificar el hosting).

## ⚠️ Notas

- La consola de H2 (`/h2-console`) queda habilitada para poder inspeccionar los datos durante
  el desarrollo; se recomienda deshabilitarla (`spring.h2.console.enabled=false`) o protegerla
  antes de un uso productivo serio.
- Este proyecto prioriza que sea simple de entender y extender. Antes de un uso con mucho
  tráfico real, sumar autenticación en el panel y mover a PostgreSQL.
