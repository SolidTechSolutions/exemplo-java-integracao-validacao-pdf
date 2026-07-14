# 🇧🇷 SolidSign API - Exemplo de Validação de Assinatura PDF (PAdES)

Este projeto demonstra a integração com a **SolidSign API** para validar assinaturas digitais PAdES em documentos PDF, retornando um relatório de validação detalhado. Suporta dois modos de operação: **batch** (lê arquivos de uma pasta local) e **form** (recebe arquivos via upload HTTP).

## Estrutura do Projeto

* **Controller:** Expõe os endpoints `/batch` (escaneia a pasta de entrada) e `/form` (recebe uploads), encaminhando para o serviço.
* **Service:** Orquestra a chamada `POST /solidsign/dsig/validation/verify-pdf`, monta o `multipart/form-data` e desserializa o relatório de validação retornado.

## Configuração (application.properties)

| Atributo | Descrição | Exemplo / Valor |
| :--- | :--- | :--- |
| `server.port` | Porta local do servidor. | `8094` |
| `solidsign.api.base-url` | URL base da SolidSign API (sem o caminho). | `https://solidsign.com.br` |
| `solidsign.api.authorization` | Token JWT de autorização (Bearer). | `Bearer eyJhbGciOiJIUzI1...` |
| `solidsign.batch.input-path` | Pasta local com os PDFs assinados a validar (modo batch). | `C:/signed_pdfs` |
| `solidsign.batch.output-path` | Pasta onde o relatório JSON de validação será salvo. | `C:/validation_results` |

## Endpoints

| Método | Rota | Descrição |
| :--- | :--- | :--- |
| `POST` | `/api/pdf/validate/batch` | Valida todos os `.pdf` da pasta de entrada e salva o relatório JSON na saída. |
| `POST` | `/api/pdf/validate/form` | Valida `.pdf` enviados como `multipart/form-data` (campo `document`) e retorna o relatório. |

## Stack
1. Java 17
2. Spring Boot 3.2.5
3. Maven 3.x+
4. RestTemplate (cliente HTTP) + Logback (logging)

## Como Executar

1. **Configurar:** Ajuste `solidsign.api.base-url`, `solidsign.api.authorization` e as pastas em `src/main/resources/application.properties`.
2. **Compilar:** `mvn clean install`
3. **Iniciar:** `mvn spring-boot:run`
4. **Testar (batch):** `curl -X POST http://localhost:8094/api/pdf/validate/batch`
5. **Testar (form):** `curl -X POST http://localhost:8094/api/pdf/validate/form -F "document=@/caminho/assinado.pdf"`

## Resposta

O relatório (`ValidationReportsResponseDTO`) contém `documentCount` e a lista `documentValidations[]`. Cada documento traz `globalIndication` (`TOTAL_PASSED`, `PASSED`, `INDETERMINATE`, `FAILED`), a quantidade de assinaturas e os detalhes de cada uma (tipo, padrão, perfil ICP-Brasil, integridade, validade do certificado, carimbos de tempo).

## Tratamento de Erros
O serviço repassa os erros **4xx/5xx** da SolidSign e registra o corpo JSON detalhado via Logback, facilitando o debug de tokens inválidos ou arquivos malformados.

---

# 🇬🇧 SolidSign API - PDF Signature Validation Example (PAdES)

This project demonstrates the integration with the **SolidSign API** to validate PAdES digital signatures on PDF documents, returning a detailed validation report. It supports two modes: **batch** (reads files from a local folder) and **form** (receives files via HTTP upload).

## Project Structure

* **Controller:** Exposes the `/batch` (scans the input folder) and `/form` (receives uploads) endpoints, delegating to the service.
* **Service:** Orchestrates the `POST /solidsign/dsig/validation/verify-pdf` call, builds the `multipart/form-data` body, and deserializes the returned validation report.

## Configuration (application.properties)

| Attribute | Description | Example / Value |
| :--- | :--- | :--- |
| `server.port` | Local server port. | `8094` |
| `solidsign.api.base-url` | Base URL of the SolidSign API (without path). | `https://solidsign.com.br` |
| `solidsign.api.authorization` | Authorization JWT Token (Bearer). | `Bearer eyJhbGciOiJIUzI1...` |
| `solidsign.batch.input-path` | Local folder with signed PDFs to validate (batch mode). | `C:/signed_pdfs` |
| `solidsign.batch.output-path` | Local folder where the JSON validation report will be saved. | `C:/validation_results` |

## Endpoints

| Method | Route | Description |
| :--- | :--- | :--- |
| `POST` | `/api/pdf/validate/batch` | Validates all `.pdf` files in the input folder and saves the JSON report to the output folder. |
| `POST` | `/api/pdf/validate/form` | Validates `.pdf` files sent as `multipart/form-data` (field `document`) and returns the report. |

## Stack
1. Java 17
2. Spring Boot 3.2.5
3. Maven 3.x+
4. RestTemplate (HTTP client) + Logback (logging)

## How to Run

1. **Configure:** Set `solidsign.api.base-url`, `solidsign.api.authorization` and the folders in `src/main/resources/application.properties`.
2. **Build:** `mvn clean install`
3. **Start:** `mvn spring-boot:run`
4. **Test (batch):** `curl -X POST http://localhost:8094/api/pdf/validate/batch`
5. **Test (form):** `curl -X POST http://localhost:8094/api/pdf/validate/form -F "document=@/path/signed.pdf"`

## Response

The report (`ValidationReportsResponseDTO`) contains `documentCount` and the `documentValidations[]` list. Each document carries `globalIndication` (`TOTAL_PASSED`, `PASSED`, `INDETERMINATE`, `FAILED`), the signature count, and the details of each one (type, standard, ICP-Brasil profile, integrity, certificate validity, timestamps).

## Error Handling
The service forwards SolidSign **4xx/5xx** errors and logs the detailed JSON body via Logback, easing the debugging of invalid tokens or malformed files.

---

# 🇪🇸 SolidSign API - Ejemplo de Validación de Firma PDF (PAdES)

Este proyecto demuestra la integración con la **SolidSign API** para validar firmas digitales PAdES en documentos PDF, devolviendo un informe de validación detallado. Admite dos modos: **batch** (lee archivos de una carpeta local) y **form** (recibe archivos vía carga HTTP).

## Estructura del Proyecto

* **Controller:** Expone los endpoints `/batch` (escanea la carpeta de entrada) y `/form` (recibe cargas), delegando al servicio.
* **Service:** Orquesta la llamada `POST /solidsign/dsig/validation/verify-pdf`, arma el `multipart/form-data` y deserializa el informe de validación devuelto.

## Configuración (application.properties)

| Atributo | Descripción | Ejemplo / Valor |
| :--- | :--- | :--- |
| `server.port` | Puerto local del servidor. | `8094` |
| `solidsign.api.base-url` | URL base de la SolidSign API (sin la ruta). | `https://solidsign.com.br` |
| `solidsign.api.authorization` | Token JWT de autorización (Bearer). | `Bearer eyJhbGciOiJIUzI1...` |
| `solidsign.batch.input-path` | Carpeta local con los PDF firmados a validar (modo batch). | `C:/signed_pdfs` |
| `solidsign.batch.output-path` | Carpeta donde se guardará el informe JSON de validación. | `C:/validation_results` |

## Endpoints

| Método | Ruta | Descripción |
| :--- | :--- | :--- |
| `POST` | `/api/pdf/validate/batch` | Valida todos los `.pdf` de la carpeta de entrada y guarda el informe JSON en la salida. |
| `POST` | `/api/pdf/validate/form` | Valida `.pdf` enviados como `multipart/form-data` (campo `document`) y devuelve el informe. |

## Stack
1. Java 17
2. Spring Boot 3.2.5
3. Maven 3.x+
4. RestTemplate (cliente HTTP) + Logback (registro)

## Cómo Ejecutar

1. **Configurar:** Ajuste `solidsign.api.base-url`, `solidsign.api.authorization` y las carpetas en `src/main/resources/application.properties`.
2. **Compilar:** `mvn clean install`
3. **Iniciar:** `mvn spring-boot:run`
4. **Probar (batch):** `curl -X POST http://localhost:8094/api/pdf/validate/batch`
5. **Probar (form):** `curl -X POST http://localhost:8094/api/pdf/validate/form -F "document=@/ruta/firmado.pdf"`

## Respuesta

El informe (`ValidationReportsResponseDTO`) contiene `documentCount` y la lista `documentValidations[]`. Cada documento trae `globalIndication` (`TOTAL_PASSED`, `PASSED`, `INDETERMINATE`, `FAILED`), la cantidad de firmas y los detalles de cada una (tipo, estándar, perfil ICP-Brasil, integridad, validez del certificado, sellos de tiempo).

## Gestión de Errores
El servicio reenvía los errores **4xx/5xx** de SolidSign y registra el cuerpo JSON detallado vía Logback, facilitando la depuración de tokens inválidos o archivos malformados.
