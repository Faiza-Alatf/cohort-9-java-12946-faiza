import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

function Dashboard() {
  const navigate = useNavigate();

  const [contacts, setContacts] = useState([]);
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(0);

  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const user = JSON.parse(localStorage.getItem("user"));

  const pageSize = 6;

  useEffect(() => {
    fetchContacts();
  }, [page, search]);

  const fetchContacts = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await api.get("/contacts", {
        params: {
          search: search,
          page: page,
          size: pageSize,
        },
      });

      setContacts(response.data.content || []);
      setTotalPages(response.data.totalPages || 0);
      setTotalElements(response.data.totalElements || 0);

    } catch (err) {
      if (err.response?.status === 401) {
        localStorage.removeItem("token");
        localStorage.removeItem("user");

        navigate("/login");
        return;
      }

      setError(
        err.response?.data?.error ||
        "Unable to load contacts."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    setSearch(e.target.value);
    setPage(0);
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");

    navigate("/login");
  };

  return (
    <div className="dashboard">

      {/* =========================
          DASHBOARD HEADER
      ========================= */}

      <header className="dashboard-header">

        <div>
          <h1>Contact Management</h1>

          <p>
            Welcome, {user?.firstName || "User"}!
          </p>
        </div>

        <div className="dashboard-actions">

          {/* Profile Button */}
          <button
            className="profile-button"
            onClick={() => navigate("/profile")}
          >
            My Profile
          </button>

          {/* Logout Button */}
          <button
            onClick={handleLogout}
            className="logout-button"
          >
            Logout
          </button>

        </div>

      </header>

      {/* =========================
          MAIN CONTENT
      ========================= */}

      <main className="dashboard-content">

        {/* Contacts Header */}

        <div className="contacts-header">

          <div>
            <h2>My Contacts</h2>

            <p>
              Manage your personal and professional contacts
            </p>
          </div>

          {/* Add Contact */}

          <button
            className="add-contact-button"
            onClick={() => navigate("/contacts/new")}
          >
            + Add Contact
          </button>

        </div>

        {/* =========================
            SEARCH & CONTACT COUNT
        ========================= */}

        <div className="contacts-toolbar">

          <input
            type="text"
            className="search-input"
            placeholder="Search by first name or last name..."
            value={search}
            onChange={handleSearch}
          />

          <span className="contact-count">
            {totalElements} Contact
            {totalElements !== 1 ? "s" : ""}
          </span>

        </div>

        {/* =========================
            LOADING
        ========================= */}

        {loading && (
          <div className="loading-state">
            <p>Loading contacts...</p>
          </div>
        )}

        {/* =========================
            ERROR
        ========================= */}

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        {/* =========================
            EMPTY STATE
        ========================= */}

        {!loading &&
          !error &&
          contacts.length === 0 && (

            <div className="empty-state">

              <h3>
                {search
                  ? "No Contacts Found"
                  : "No Contacts Yet"}
              </h3>

              <p>
                {search
                  ? "No contacts match your search."
                  : "You haven't added any contacts yet."}
              </p>

              {!search && (
                <button
                  onClick={() =>
                    navigate("/contacts/new")
                  }
                >
                  Add Your First Contact
                </button>
              )}

            </div>
          )}

        {/* =========================
            CONTACT CARDS
        ========================= */}

        {!loading &&
          !error &&
          contacts.length > 0 && (

            <>

              <div className="contacts-grid">

                {contacts.map((contact) => (

                  <div
                    className="contact-card"
                    key={contact.id}
                  >

                    <h3>
                      {contact.firstName}{" "}
                      {contact.lastName}
                    </h3>

                    <p>
                      {contact.title || "No title"}
                    </p>

                    <p>
                      <strong>Work Email:</strong>{" "}
                      {contact.workEmail || "N/A"}
                    </p>

                    <p>
                      <strong>Work Phone:</strong>{" "}
                      {contact.workPhone || "N/A"}
                    </p>

                    <button
                      onClick={() =>
                        navigate(
                          `/contacts/${contact.id}`
                        )
                      }
                    >
                      View Details
                    </button>

                  </div>

                ))}

              </div>

              {/* =========================
                  PAGINATION
              ========================= */}

              {totalPages > 1 && (

                <div className="pagination">

                  <button
                    disabled={page === 0}
                    onClick={() =>
                      setPage(page - 1)
                    }
                  >
                    Previous
                  </button>

                  <span>
                    Page {page + 1} of {totalPages}
                  </span>

                  <button
                    disabled={
                      page >= totalPages - 1
                    }
                    onClick={() =>
                      setPage(page + 1)
                    }
                  >
                    Next
                  </button>

                </div>

              )}

            </>

          )}

      </main>

    </div>
  );
}

export default Dashboard;