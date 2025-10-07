# TDLight Bot

A Kotlin-based Telegram bot application using the TDLight library for interacting with Telegram's API. This project demonstrates how to connect to Telegram, authenticate users, and retrieve channel information and messages.

## Features

- **Telegram Authentication**: Console-based phone number authentication
- **Channel Management**: Retrieve subscribed channels
- **Message Retrieval**: Get the last message from each channel
- **Clean Architecture**: Organized with domain, data, and use case layers
- **Coroutines Support**: Asynchronous operations using Kotlin coroutines

## Project Structure

```
src/main/kotlin/
├── Main.kt                                    # Application entry point
└── org/exxuslee/
    ├── common/
    │   └── StringExtensions.kt                # String utility extensions
    ├── data/
    │   └── tdlight/
    │       ├── TdLightClientProvider.kt       # TDLight client initialization
    │       └── TdLightTelegramRepository.kt   # Telegram API repository
    └── domain/
        ├── model/
        │   └── Chat.kt                        # Chat data model
        ├── repository/
        │   └── TelegramRepository.kt          # Repository interface
        └── usecase/
            ├── GetChatHistoryUseCase.kt       # Get chat history use case
            ├── GetLastMessageUseCase.kt       # Get last message use case
            ├── GetSubscribedChannelsUseCase.kt # Get subscribed channels use case
            └── SearchAndEnsureJoinedUseCase.kt # Search and join channels use case
```

## Prerequisites

- **Java 21** or higher
- **Kotlin 2.2.0**
- **Telegram API Credentials**:
  - API ID
  - API Hash
  - API User ID

## Setup

### 1. Get Telegram API Credentials

1. Go to [my.telegram.org](https://my.telegram.org)
2. Log in with your phone number
3. Go to "API development tools"
4. Create a new application
5. Note down your `API ID` and `API Hash`

### 2. Environment Configuration

Create a `.env` file in the project root with your Telegram API credentials:

```env
API_ID=your_api_id_here
API_HASH=your_api_hash_here
API_USER_ID=your_user_id_here
```

Alternatively, you can set these as environment variables:

```bash
export API_ID=your_api_id_here
export API_HASH=your_api_hash_here
export API_USER_ID=your_user_id_here
```

### 3. Build and Run

```bash
# Build the project
./gradlew build

# Run the application
./gradlew run
```

Or on Windows:

```cmd
gradlew.bat build
gradlew.bat run
```

## Usage

1. **Start the application**: Run the main function
2. **Authentication**: Follow the console prompts to authenticate with your phone number
3. **Channel Discovery**: The bot will automatically retrieve your subscribed channels
4. **Message Retrieval**: For each channel, it will fetch and display the last message

## Dependencies

- **TDLight Java**: Telegram client library
- **Kotlin Coroutines**: For asynchronous operations
- **Dotenv Kotlin**: Environment variable management
- **SLF4J Simple**: Logging framework

## Architecture

This project follows Clean Architecture principles:

- **Domain Layer**: Contains business logic, models, and use cases
- **Data Layer**: Handles external API interactions and data sources
- **Presentation Layer**: Main application entry point

## Development

### Building

```bash
./gradlew build
```

### Testing

```bash
./gradlew test
```

### Running

```bash
./gradlew run
```

## Configuration

The application uses the following configuration:

- **Session Storage**: Sessions are stored in `tdlib-session-id{API_USER_ID}/` directory
- **Database**: SQLite database for session data
- **Downloads**: Downloaded files are stored in the session directory
- **Logging**: Configured via `logback.xml`

## Troubleshooting

### Common Issues

1. **Authentication Failed**: Ensure your API credentials are correct
2. **Session Issues**: Delete the session directory and re-authenticate
3. **Network Issues**: Check your internet connection and firewall settings

### Logs

Check the console output for detailed error messages and authentication prompts.

## License

This project is for educational and development purposes. Please ensure compliance with Telegram's Terms of Service when using this bot.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## Support

For issues and questions, please check the [TDLight documentation](https://github.com/tdlight-team/tdlight) or create an issue in this repository.
