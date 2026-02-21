const API_BASE = '/api/v1';

const api = {
    async request(endpoint, options = {}) {
        const token = localStorage.getItem('token');
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const response = await fetch(`${API_BASE}${endpoint}`, {
            ...options,
            headers
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.message || 'Something went wrong');
        }

        return result;
    },

    auth: {
        login: (credentials) => api.request('/auth/login', {
            method: 'POST',
            body: JSON.stringify(credentials)
        }),
        register: (details) => api.request('/auth/register', {
            method: 'POST',
            body: JSON.stringify(details)
        }),
        getMe: () => api.request('/auth/me')
    },


    students: {
        getAll: () => api.request('/students'),
        getPage: (page, size = 10) => api.request(`/students/page?page=${page}&size=${size}`),
        getById: (id) => api.request(`/students/${id}`),
        create: (data) => api.request('/students', {
            method: 'POST',
            body: JSON.stringify(data)
        }),
        update: (id, data) => api.request(`/students/${id}`, {
            method: 'PUT',
            body: JSON.stringify(data)
        }),
        delete: (id) => api.request(`/students/${id}`, {
            method: 'DELETE'
        }),
        search: (name, email, page = 0, size = 10) => {
            let url = `/students/search?page=${page}&size=${size}`;
            if (name) url += `&name=${encodeURIComponent(name)}`;
            if (email) url += `&email=${encodeURIComponent(email)}`;
            return api.request(url);
        },
        myProfile: () => api.request('/students/my-profile')
    },
    audit: {
        getLogs: () => api.request('/audit/logs'),
        getStudentLogs: (id) => api.request(`/audit/student/${id}`)
    }
};

