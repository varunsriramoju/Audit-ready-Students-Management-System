const auth = {
    init() {
        this.checkAuth();
        this.bindEvents();
    },

    checkAuth() {
        const token = localStorage.getItem('token');
        const user = localStorage.getItem('user');
        const role = localStorage.getItem('role');

        if (token && user && role) {
            this.showDashboard(user, role);
        } else {
            this.showAuth();
        }
    },

    bindEvents() {
        document.getElementById('login-form').addEventListener('submit', (e) => this.handleLogin(e));
        document.getElementById('register-form').addEventListener('submit', (e) => this.handleRegister(e));
        document.getElementById('logout-btn').addEventListener('click', () => this.handleLogout());

        document.getElementById('show-register').addEventListener('click', () => {
            document.getElementById('login-form-container').classList.add('hidden');
            document.getElementById('register-form-container').classList.remove('hidden');
        });

        document.getElementById('show-login').addEventListener('click', () => {
            document.getElementById('register-form-container').classList.add('hidden');
            document.getElementById('login-form-container').classList.remove('hidden');
        });
    },

    async handleLogin(e) {
        e.preventDefault();
        const username = document.getElementById('login-username').value;
        const password = document.getElementById('login-password').value;

        try {
            const result = await api.auth.login({ username, password });
            localStorage.setItem('token', result.data.token);
            localStorage.setItem('user', result.data.username);
            localStorage.setItem('role', result.data.role);
            this.showDashboard(result.data.username, result.data.role);
            app.loadStudents();
        } catch (error) {
            alert(error.message);
        }
    },

    async handleRegister(e) {
        e.preventDefault();
        const username = document.getElementById('reg-username').value;
        const password = document.getElementById('reg-password').value;
        const email = document.getElementById('reg-email').value;
        const role = document.getElementById('reg-role').value;

        try {
            await api.auth.register({ username, password, email, role });
            alert('Registration successful! Please login.');
            document.getElementById('show-login').click();
        } catch (error) {

            alert(error.message);
        }
    },

    handleLogout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        localStorage.removeItem('role');
        this.showAuth();
    },

    showDashboard(username, role) {
        document.getElementById('auth-section').classList.add('hidden');
        document.getElementById('dashboard-section').classList.remove('hidden');
        document.getElementById('user-display').textContent = `Welcome, ${username}`;

        const roleBadge = document.getElementById('role-badge');
        if (roleBadge) {
            roleBadge.textContent = role.replace('ROLE_', '');
            roleBadge.className = `role-badge ${role.toLowerCase()}`;
        }

        this.applyRBAC(role);
    },

    applyRBAC(role) {
        const elements = document.querySelectorAll('[data-role]');
        elements.forEach(el => {
            const allowedRoles = el.getAttribute('data-role').split(',');
            // Map new roles to visibility
            if (allowedRoles.includes(role) || allowedRoles.includes('ANY')) {
                el.classList.remove('rbac-hidden');
            } else {
                el.classList.add('rbac-hidden');
            }
        });

        // Special handling for Student role: hide student list if needed (though API blocks it)
        const dashboardTitle = document.querySelector('.card-header h2');
        if (role === 'STUDENT' && dashboardTitle) {
            dashboardTitle.textContent = 'My Academic Profile';
            document.getElementById('search-input').parentElement.classList.add('hidden');
        } else if (dashboardTitle) {
            dashboardTitle.textContent = 'Student Records';
            document.getElementById('search-input').parentElement.classList.remove('hidden');
        }
    },


    showAuth() {
        document.getElementById('dashboard-section').classList.add('hidden');
        document.getElementById('auth-section').classList.remove('hidden');
    }

};

auth.init();
