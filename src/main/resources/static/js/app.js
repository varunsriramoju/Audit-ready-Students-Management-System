const app = {
    state: {
        currentPage: 0,
        pageSize: 5,
        searchName: '',
        searchEmail: ''
    },

    init() {
        this.bindEvents();
        if (localStorage.getItem('token')) {
            this.loadStudents();
        }
    },

    bindEvents() {
        document.getElementById('add-student-btn').addEventListener('click', () => this.showModal());
        document.getElementById('close-modal').addEventListener('click', () => this.hideModal());
        document.getElementById('student-form').addEventListener('submit', (e) => this.handleSaveStudent(e));

        // Search with debounce
        let timeout = null;
        document.getElementById('search-input').addEventListener('input', (e) => {
            clearTimeout(timeout);
            timeout = setTimeout(() => {
                this.state.searchName = e.target.value;
                this.state.currentPage = 0;
                this.loadStudents();
            }, 500);
        });
        // View Switching
        document.getElementById('view-records-btn').addEventListener('click', () => this.switchView('records'));
        document.getElementById('view-audit-btn').addEventListener('click', () => this.switchView('audit'));
        document.getElementById('refresh-audit-btn').addEventListener('click', () => this.loadAuditLogs());
        document.getElementById('close-audit-modal').addEventListener('click', () => this.hideAuditModal());
    },

    async loadStudents() {
        const role = localStorage.getItem('role');
        if (role === 'ROLE_STUDENT') {
            try {
                const result = await api.students.myProfile();
                this.renderStudents([result.data]);
                document.getElementById('pagination').innerHTML = '';
            } catch (error) {
                console.error('Failed to load profile', error);
                document.getElementById('students-list').innerHTML = `<div style="text-align: center; padding: 2rem; color: var(--danger);">Error loading your profile. Please ensure you have a student record linked to your email.</div>`;
            }
            return;
        }

        try {
            const result = await api.students.search(
                this.state.searchName,
                this.state.searchEmail,
                this.state.currentPage,
                this.state.pageSize
            );
            this.renderStudents(result.data.content);
            this.renderPagination(result.data);
            if (typeof auth !== 'undefined') auth.applyRBAC(role);
        } catch (error) {


            console.error('Failed to load students', error);
        }
    },

    renderStudents(students) {
        const container = document.getElementById('students-list');
        if (students.length === 0) {
            container.innerHTML = '<div style="text-align: center; padding: 2rem; color: var(--text-muted);">No students found.</div>';
            return;
        }

        container.innerHTML = `
            <table style="width: 100%; border-collapse: collapse;">
                <thead>
                    <tr style="text-align: left; border-bottom: 2px solid var(--border);">
                        <th style="padding: 1rem;">Name</th>
                        <th style="padding: 1rem;">Email</th>
                        <th style="padding: 1rem;">Department</th>
                        <th style="padding: 1rem;">Year</th>
                        <th style="padding: 1rem;">CGPA</th>
                        <th style="padding: 1rem; text-align: right;">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    ${students.map(s => `
                        <tr style="border-bottom: 1px solid var(--border); transition: background 0.2s;" onmouseover="this.style.background='#f8fafc'" onmouseout="this.style.background='transparent'">
                            <td style="padding: 1rem; font-weight: 500;">${s.name}</td>
                            <td style="padding: 1rem;">${s.email}</td>
                            <td style="padding: 1rem;">${s.department}</td>
                            <td style="padding: 1rem;">${s.year}</td>
                            <td style="padding: 1rem;"><span style="background: var(--info); color: white; padding: 0.25rem 0.5rem; border-radius: 4px; font-size: 0.75rem;">${s.cgpa || 'N/A'}</span></td>
                             <td style="padding: 1rem; text-align: right;">
                                 <button onclick="app.editStudent(${s.id})" class="btn btn-secondary" style="width: auto; padding: 0.25rem 0.75rem; font-size: 0.75rem; margin-right: 0.5rem; background: var(--secondary); color: white;" data-role="ADMIN,STAFF">Edit</button>
                                 <button onclick="app.showStudentAudit(${s.id}, '${s.name}')" class="btn btn-info" style="width: auto; padding: 0.25rem 0.75rem; font-size: 0.75rem; margin-right: 0.5rem; background: var(--info); color: white;" data-role="ADMIN,STAFF">Audit</button>
                                 <button onclick="app.deleteStudent(${s.id})" class="btn btn-danger" style="width: auto; padding: 0.25rem 0.75rem; font-size: 0.75rem; background: var(--danger); color: white;" data-role="ADMIN">Delete</button>
                             </td>

                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    },

    renderPagination(pageData) {
        const container = document.getElementById('pagination');
        let html = '';

        for (let i = 0; i < pageData.totalPages; i++) {
            html += `<button onclick="app.changePage(${i})" class="btn" style="width: auto; padding: 0.5rem 1rem; ${i === this.state.currentPage ? 'background: var(--primary); color: white;' : 'background: white; border: 1px solid var(--border);'}">${i + 1}</button>`;
        }

        container.innerHTML = html;
    },

    changePage(page) {
        this.state.currentPage = page;
        this.loadStudents();
    },

    showModal(student = null) {
        const modal = document.getElementById('student-modal');
        const form = document.getElementById('student-form');
        const title = document.getElementById('modal-title');

        form.reset();
        document.getElementById('student-id').value = '';
        title.textContent = 'Add Student';

        if (student) {
            title.textContent = 'Edit Student';
            document.getElementById('student-id').value = student.id;
            document.getElementById('std-name').value = student.name;
            document.getElementById('std-email').value = student.email;
            document.getElementById('std-phone').value = student.phone || '';
            document.getElementById('std-dept').value = student.department;
            document.getElementById('std-year').value = student.year;
            document.getElementById('std-cgpa').value = student.cgpa || '';
            document.getElementById('std-address').value = student.address || '';
        }

        const role = localStorage.getItem('role');
        const isStaff = role === 'STAFF';

        // Disable sensitive fields for staff
        document.getElementById('std-email').disabled = isStaff;

        modal.classList.remove('hidden');
    },

    hideModal() {
        document.getElementById('student-modal').classList.add('hidden');
    },

    async handleSaveStudent(e) {
        e.preventDefault();
        const id = document.getElementById('student-id').value;
        const data = {
            name: document.getElementById('std-name').value,
            email: document.getElementById('std-email').value,
            phone: document.getElementById('std-phone').value,
            department: document.getElementById('std-dept').value,
            year: parseInt(document.getElementById('std-year').value),
            cgpa: parseFloat(document.getElementById('std-cgpa').value),
            address: document.getElementById('std-address').value
        };

        try {
            if (id) {
                await api.students.update(id, data);
            } else {
                await api.students.create(data);
            }
            this.hideModal();
            this.loadStudents();
        } catch (error) {
            alert(error.message);
        }
    },

    async editStudent(id) {
        try {
            const result = await api.students.getById(id);
            this.showModal(result.data);
        } catch (error) {
            alert(error.message);
        }
    },

    async deleteStudent(id) {
        if (!confirm('Are you sure you want to delete this student?')) return;
        try {
            await api.students.delete(id);
            this.loadStudents();
        } catch (error) {
            alert(error.message);
        }
    },

    switchView(view) {
        const recordsView = document.getElementById('students-list').parentElement;
        const auditView = document.getElementById('audit-section');
        const recordsBtn = document.getElementById('view-records-btn');
        const auditBtn = document.getElementById('view-audit-btn');

        if (view === 'records') {
            recordsView.classList.remove('hidden');
            auditView.classList.add('hidden');
            recordsBtn.style.color = 'var(--primary)';
            recordsBtn.style.fontWeight = '700';
            auditBtn.style.color = 'var(--text-muted)';
            auditBtn.style.fontWeight = '400';
        } else {
            recordsView.classList.add('hidden');
            auditView.classList.remove('hidden');
            recordsBtn.style.color = 'var(--text-muted)';
            recordsBtn.style.fontWeight = '400';
            auditBtn.style.color = 'var(--primary)';
            auditBtn.style.fontWeight = '700';
            this.loadAuditLogs();
        }
    },

    async loadAuditLogs() {
        try {
            const container = document.getElementById('audit-list');
            container.innerHTML = '<div style="text-align: center; padding: 2rem;">Loading logs...</div>';

            const result = await api.audit.getLogs();
            this.renderAuditLogs(result.data, 'audit-list');
        } catch (error) {
            console.error('Failed to load logs', error);
            document.getElementById('audit-list').innerHTML = `<div class="error-msg">${error.message}</div>`;
        }
    },

    async showStudentAudit(id, name) {
        try {
            document.getElementById('audit-modal-title').textContent = `Audit History: ${name}`;
            const container = document.getElementById('student-audit-list');
            container.innerHTML = '<div style="text-align: center; padding: 2rem;">Loading history...</div>';
            document.getElementById('student-audit-modal').classList.remove('hidden');

            const result = await api.audit.getStudentLogs(id);
            this.renderAuditLogs(result.data, 'student-audit-list');
        } catch (error) {
            alert('Failed to load audit history: ' + error.message);
        }
    },

    hideAuditModal() {
        document.getElementById('student-audit-modal').classList.add('hidden');
    },

    renderAuditLogs(logs, containerId) {
        const container = document.getElementById(containerId);
        if (logs.length === 0) {
            container.innerHTML = '<div style="text-align: center; padding: 2rem; color: var(--text-muted);">No audit logs found.</div>';
            return;
        }

        container.innerHTML = `
            <table style="width: 100%; border-collapse: collapse;">
                <thead>
                    <tr style="text-align: left; border-bottom: 2px solid var(--border);">
                        <th style="padding: 1rem;">Time</th>
                        <th style="padding: 1rem;">Action</th>
                        <th style="padding: 1rem;">User</th>
                        <th style="padding: 1rem;">Details</th>
                    </tr>
                </thead>
                <tbody>
                    ${logs.map(log => `
                        <tr style="border-bottom: 1px solid var(--border);">
                            <td style="padding: 1rem; font-size: 0.85rem; color: var(--text-muted);">${new Date(log.changedAt).toLocaleString()}</td>
                            <td style="padding: 1rem;"><span class="role-badge ${log.action.toLowerCase()}">${log.action}</span></td>
                            <td style="padding: 1rem; font-weight: 500;">${log.changedBy}</td>
                            <td style="padding: 1rem;">
                                <div style="font-size: 0.9rem; font-weight: 600; color: var(--primary);">${log.entityName} #${log.entityId}</div>
                                <div style="font-size: 0.85rem; margin-top: 0.25rem;">${log.diff}</div>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    }
};

app.init();
