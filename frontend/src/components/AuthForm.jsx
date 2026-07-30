function AuthForm({
    mode,
    setMode,
    name,
    setName,
    email,
    setEmail,
    password,
    setPassword,
    handleRegister,
    handleLogin,
    message,
                  }) {
    return (
        <div className="app">
            <div className="card">
                <h1>Task Manager</h1>

                <div className="tabs">
                    <button
                        type="button"
                        className={mode === "login" ? "active" : "secondary"}
                        onClick={() => setMode("login")}
                    >
                        Login
                    </button>

                    <button
                        type="button"
                        className={mode === "register" ? "active" : "secondary"}
                        onClick={() => setMode("register")}
                        >
                        Register
                    </button>
                </div>

                {mode === "register" ? (
                    <form onSubmit={handleRegister}>
                        <label>Name</label>
                        <input
                            type="text"
                            value={name}
                            onChange={(event) => setName(event.target.value)}
                            placeholder="Enter your name"
                        />

                        <label>Email</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(event) => setEmail(event.target.value)}
                            placeholder="Enter your email"
                        />

                        <label>Password</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(event) => setPassword(event.target.value)}
                            placeholder="Enter your password"
                        />

                        <button type="submit">Create Account</button>
                    </form>
                ) : (
                    <form onSubmit={handleLogin}>
                        <label>Email</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(event) => setEmail(event.target.value)}
                            placeholder="Enter your email"
                        />

                        <label>Password</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(event) => setPassword(event.target.value)}
                            placeholder="Enter your password"
                        />

                      <button type="submit">Login</button>
                    </form>
                    )}
                {message && <div className="message">{message}</div>}
            </div>
        </div>
    );
}

export default AuthForm;