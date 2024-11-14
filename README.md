# TarotReading App - Technical Documentation

## Table of Contents
- [Overview](#overview)
- [Installation](#installation)
- [Technical Architecture](#technical-architecture)
- [Key Features](#key-features)
- [Implementation Details](#implementation-details)
- [Flow Diagrams](#flow-diagrams)
- [Core Components](#core-components)


## Overview

TarotReading is an Android application that combines traditional tarot card reading with AI-powered interpretations using GPT-4. The app supports both digital card selection and physical card recognition through the device's camera.


## Installation

1. Clone the repository
```bash
git clone https://github.com/yunxin119/TarotReading.git
```

2. Add API key in `local.properties`:
```properties
OPENAI_API_KEY=your_api_key_here
```

3. Sync Gradle and run

## API Integration

### OpenAI GPT-4 Integration
```java
JSONObject jsonObject = new JSONObject();
jsonObject.put("model", "gpt-4");
jsonObject.put("temperature", 0);
jsonObject.put("max_tokens", 300);
```

### Request Headers
```java
Map<String, String> params = new HashMap<>();
params.put("Content-Type", "application/json");
params.put("Authorization", "Bearer " + apiKey);
```

## Contributing

1. Fork the repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Create Pull Request

## Development Setup

### Required Dependencies
```gradle
dependencies {
    implementation 'com.android.volley:volley:1.2.1'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
}
```


## Technical Architecture

### Core Components
```
src/main/java/com/group5/tarotreading/
├── AIAnswerActivity.java          # AI-powered answer processing
├── CameraActivity.java           # Camera handling for physical cards
├── MainActivity.java             # Application entry point
├── ResultActivity.java          # Display reading results
│
├── card/                        # Card Management Package
│   ├── Card.java               # Card entity model
│   ├── CardPick.java           # Card selection logic
│   ├── CardPickActivity.java   # Card selection UI
│   └── CardPickView.java       # Custom card view implementation
│
├── question/                    # Question Handling Package
│   ├── AskFragment.java        # Question input interface
│   ├── QuestionActivity.java   # Question processing
│   └── TodayFragment.java      # Daily reading interface
│
├── result/                     # Result Display Package
│   └── AnswerPage.java        # Tarot reading results view
│
├── user/                       # User Management Package
│   ├── ChangePasswordActivity.java  # Password modification
│   ├── LoginActivity.java          # User authentication
│   ├── RegisterActivity.java       # User registration
│   └── UserProfile.java           # User profile management
│
└── utils/                      # Utility Package
    ├── OpenAIHelper.java           # GPT API integration
    ├── OpenAIImageOptimizer.java   # Image optimization
    └── VisionAIHelper.java         # Computer vision processing
```

## Key Features

### 1. Dynamic Card Management System
- Custom `CardPickView` implementation for interactive card selection
- Card state management (face up/down, rotation)
- Asset management for card images
```java
public class Card {
    private Bitmap frontImage;
    private Bitmap backImage;
    private boolean isFaceUp = false;
    private boolean isReversed;
    
    public void flip() {
        Random random = new Random();
        this.isReversed = random.nextBoolean();
        frontImage = isReversed ? getRotatedBitmap(frontImage) : frontImage;
        isFaceUp = !isFaceUp;
    }
}
```

### 2. Spread Pattern System
The app supports multiple spread patterns:
- OneCard: Daily reading (1 card)
- TimeFlow: Past-Present-Future (3 cards)
- LoverCross: Relationship analysis (5 cards)
- SixStars: Comprehensive situation analysis (6 cards)
- TwoSelection: Decision making (5 cards)
- Marlboro: Situation development (5 cards)

### 3. AI Integration
Implements GPT-4 for:
```java
public class OpenAIHelper {
    private static final String url = "https://api.openai.com/v1/chat/completions";
    
    public void generateResponse(String prompt, AIResponseListener listener) {
        // API integration for spread selection and interpretation
    }
}
```
### 4. User Registration System
- Email-based registration
- Input validation
- Secure password storage
- Firebase Firestore integration
- Auto-login after registration

##### Registration Flow
```java
public class RegisterActivity extends AppCompatActivity {
private FirebaseFirestore db = FirebaseFirestore.getInstance();
private CollectionReference collectionReference = db.collection("Tarot-Reading");

    // Registration process
    private boolean register() {
        String username = eusername.getText().toString().trim();
        String email = eemail.getText().toString().trim();
        String password = epassword.getText().toString().trim();
        
        if (CheckAllFields(username, email, password)) {
            SaveDataToNewDocument();
            return true;
        }
        return false;
    }

    // Data validation
    private boolean CheckAllFields(String username, String email, String password) {
        if (TextUtils.isEmpty(username)) {
            eusername.setError("Please enter name");
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            eemail.setError("please enter proper email");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            epassword.setError("please enter proper password");
            return false;
        }
        return true;
    }
}
```
##### Database Structure
```java
Firestore/
└── Tarot-Reading/
└── Users/
├── username: String
├── email: String
└── password: String
```
##### User Data Management
```java
private void SaveDataToNewDocument() {
Map<String, Object> user = new HashMap<>();
user.put("username", username);
user.put("email", email);
user.put("password", password);

    collectionReference.add(user)
        .addOnSuccessListener(documentReference -> {
            String docId = documentReference.getId();
        });
}
```
## Implementation Details

### Card Selection System
The card selection system uses a custom view implementation:
```java
public class CardPickView extends View {
    private List<Card> cardList;
    private float overlapAmount = 100f;
    private GestureDetector gestureDetector;
    
    @Override
    protected void onDraw(Canvas canvas) {
        // Custom drawing logic for card display
    }
    
    private void handleCardSelection(float touchX, float touchY) {
        // Card selection logic
    }
}
```

### Question Processing Flow
1. User inputs question in `QuestionActivity`
2. Question analyzed by GPT-4 to determine spread type
3. Spread type determines card selection interface
4. Selected cards processed for interpretation

### Spread Selection Logic
```java
switch (spreadType.toLowerCase()) {
    case "lovercross":
        pickcard = 5;
        break;
    case "sixstars":
        pickcard = 6;
        cutcard = true;
        break;
    // ... other spread types
}
```

## Flow Diagrams

### Main User Flow
```
User Input → Question Analysis → Spread Selection → Card Selection → Interpretation
```

### Card Selection Flow
```
Initialize Cards → Shuffle → Display → User Selection → Flip Animation → Position Cards
```

### AI Integration Flow
```
Question Input → GPT-4 Analysis → Spread Recommendation → Card Selection → 
Interpretation Request → Display Results
```

## Core Components

### CardPickView
Custom view handling:
- Card visualization
- Touch interaction
- Animation
- State management

### OpenAIHelper
Manages:
- API communication
- Response parsing
- Error handling
- Interpretation generation

### Question Processing
Implements:
- Input validation
- Pattern recognition
- Spread selection
- Flow management

## License

This project is licensed under the MIT License. See LICENSE.md for details.

## Support

For support, please open an issue in the repository or contact the development team.
