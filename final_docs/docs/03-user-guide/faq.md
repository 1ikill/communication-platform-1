## FAQ & Troubleshooting

### Frequently Asked Questions

#### General

**Q: What is the Communication Platform?**

A: A unified backend system that aggregates Telegram, Discord, and Gmail into a single API with AI-powered message personalization. It allows managing multiple communication channels from one place.

---

**Q: What platforms are supported?**

A: Currently supports Telegram (via TDLib), Discord (via bot integration), and Gmail (via OAuth2).

---

**Q: Do I need API credentials for each platform?**

A: Yes. You need Telegram API credentials from https://my.telegram.org/apps, Discord bot token from Discord Developer Portal, Google OAuth credentials for Gmail, and an OpenAI API key for AI personalization.

---

#### Account & Access

**Q: How do I create an account?**

A: Send a POST request to `/accounts/users/auth/register` with email, username, fullName, and password. See README for example.

---

**Q: How does authentication work?**

A: JWT-based authentication. Login at `/accounts/users/auth/login` to receive access and refresh tokens. Include the access token in `Authorization: Bearer TOKEN` header for all requests.

---

#### Features

**Q: Can I send messages to multiple platforms simultaneously?**

A: Yes, use the broadcast endpoint at `/messages/broadcast` via Main Service (port 8083). You can send personalized messages across platforms in one request.

---

### Troubleshooting

#### Common Issues

| Problem | Possible Cause | Solution |
|---------|---------------|----------|
| Services won't start | Missing environment variables | Check `.env` file has all required variables (POSTGRES_PASSWORD, JWT_SECRET, API keys) |
| Database connection fails | PostgreSQL not running | Run `docker-compose ps postgres` to verify, check credentials match |
| 401 Unauthorized errors | Expired or invalid JWT | Login again to get fresh access token, check JWT_SECRET is set |
| Health check failures | Services still initializing | Wait 1-2 minutes for full startup, check `docker-compose logs` |
| Telegram auth fails | Wrong API credentials | Verify apiId and apiHash from https://my.telegram.org/apps |

#### Error Messages

| Error Code/Message | Meaning | How to Fix |
|-------------------|---------|------------|
| "Invalid credentials" | Wrong username/password during login | Check credentials, reset password if needed |
| "Token expired" | JWT access token exceeded expiration time | Use refresh token endpoint or login again |
| "Service unavailable" | Target service is down or unreachable | Check service health at `/actuator/health`, restart if needed |

#### Browser-Specific Issues

| Browser | Known Issue | Workaround |
|---------|-------------|------------|
| All | Gmail OAuth redirect | Ensure `GOOGLE_REDIRECT_URI` in `.env` matches OAuth settings in Google Cloud Console |
| Chrome/Edge | CORS issues on localhost | Use Swagger UI for testing or configure CORS properly |

### Getting Help

#### Self-Service Resources

- [API Documentation](http://localhost:8082/swagger-ui.html) - Interactive Swagger UI per service
- [README](../../README.md) - Setup and usage guide
- [Technical Documentation](../02-technical/index.md) - Architecture details

#### Contact Support

| Channel | Response Time | Best For |
|---------|--------------|----------|
| GitHub Issues | 1-2 days | Bug reports, feature requests |
| Service Logs | Immediate | Debugging runtime errors |
| rybin.com.platform@gmail.com| 8-12 hours | Bug reports, feature requests |

#### Reporting Bugs

When reporting a bug, please include:

1. **Steps to reproduce** - API endpoint, request body, headers
2. **Expected behavior** - What should happen?
3. **Actual behavior** - Error message, status code, response
4. **Service logs** - Run `docker-compose logs [service-name]`
5. **Environment** - OS, Docker version, service versions

Submit bug reports via GitHub Issues or check service logs with `docker-compose logs -f`.