package notes.common.auth

import org.springframework.stereotype.Service

@Service
class AuthService {

    fun getUsername(): String {
        return "Neha"//TODO implement authentication and authorization
    }
}