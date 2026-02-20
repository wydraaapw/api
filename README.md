# Instrukcja uruchomienia

# Wymagania środowiskowe
•	System operacyjny z rodziny Linux (np. Ubuntu, Debian) lub Windows/macOS

•	Zainstalowane oprogramowanie Docker Engine oraz narzędzie Docker Compose

•	Zainstalowany system kontroli wersji Git.

# Pobranie kodu ze zdalnego repozytorium

W celu pobrania kodu wykonaj w terminalu:

```bash
git clone https://github.com/wydraaapw/api.git
git clone https://github.com/wydraaapw/ui.git
```

Następnie przejdź do folderu `api`:

```bash
cd api
```

# Konfiguracja aplikacji
W repozytorium „api” aplikacji serwerowej znajduje się przykładowy plik z wymaganymi zmiennymi środowiskowymi .env.example.
Przed uruchomieniem aplikacji należy utworzyć i uzupełnić plik .env w tym samym katalogu. Wymagane zmienne środowiskowe przedstawiono w poniższej tabeli:

## Zmienne środowiskowe

| Nazwa zmiennej            | Opis | Przykładowa wartość |
|---------------------------|------|---------------------|
| `POSTGRES_USER` | Nazwa użytkownika bazy danych PostgreSQL, wykorzystywana do inicjalizacji kontenera oraz nawiązania połączenia z bazą danych przez aplikację serwerową. | `api_app_user` |
| `POSTGRES_APP_PASSWORD` | Hasło użytkownika bazy danych. | `Example#$231` |
| `POSTGRES_DB` | Nazwa bazy danych, która zostanie utworzona przy pierwszym uruchomieniu kontenera oraz do której będzie łączyć się aplikacja. | `Inzynierka_db` |
| `MAIL_USERNAME` | Adres e-mail konta nadawczego wykorzystywanego przez system do wysyłania powiadomień (np. rejestracja konta). | `example@gmail.com` |
| `MAIL_PASSWORD` | Hasło do konta pocztowego (w przypadku Gmail jest to dedykowane hasło aplikacji). | `app_password` |
| `APP_JWT_SECRET` | Prywatny klucz kryptograficzny używany do podpisywania i weryyfikacji tokenów JWT (JSON Web Token). Zalecane minimum 32 znaki w formacie Base64. | `3K7F9Kk1xZ0WmQ2N8rT6AqBvYpLJcH9eX5M4sD2R1Z8=` |
| `ADMIN_ACCOUNT_EMAIL` | Adres e-mail dla konta administratora, które jest tworzone automatycznie podczas inicjalizacji systemu. | `adm_mail@example.com` |
| `ADMIN_ACCOUNT_PASSWORD` | Hasło początkowe dla konta administratora. | `Example4123` |
| `BACKEND_PUBLIC_URL` | Adres sieciowy, pod którym dostępny jest interfejs API aplikacji serwerowej. Port musi być zgodny z portem zewnętrznym zdefiniowanym w pliku `docker-compose.prod.yml` (domyślnie `8080`). | `http://<IP_SERWERA>:<PORT>` |
| `FRONTEND_PUBLIC_URL` | Adres, pod którym aplikacja kliencka jest dostępna dla użytkowników. Wykorzystywana przez backend do konfiguracji CORS. Port musi być zgodny z portem zewnętrznym zdefiniowanym w pliku `docker-compose.prod.yml` (domyślnie `80`). | `http://<IP_SERWERA>:<PORT>` |

## Uruchomienie aplikacji

Po skonfigurowaniu środowiska należy uruchomić proces budowania obrazów oraz startu usług.

Przed wykonaniem polecenia upewnij się, że porty `80` oraz `8080` są wolne (domyślne porty jeśli nie zostały zmodyfikowane w docker-compose.prod.yml).

W folderze `api` wykonaj polecenie:

```bash
docker-compose -f docker-compose.prod.yml up -d --build
```

Po pomyślnym uruchomieniu aplikacja będzie dostępna pod adresem:

```
http://<IP_SERWERA>:<PORT>
```

### Przykład uruchomienia lokalnego (port 80):

```
http://localhost
```


