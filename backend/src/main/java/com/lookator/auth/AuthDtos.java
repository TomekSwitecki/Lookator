package com.lookator.auth;

import java.util.UUID;

record AuthRequest(String email, String password) {}
record AuthResponse(String token, UUID userId, String email) {}
