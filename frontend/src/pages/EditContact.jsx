
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../services/api";

function EditContact() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    title: "",
    workEmail: "",
    personalEmail: "",
    workPhone: "",
    homePhone: "",
    personalPhone: "",
  });

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchContact();
  }, [id]);

  const fetchContact = async () => {
    try {
      setLoading(true);
      setError("");

      console.log("FETCHING CONTACT:", id);

      const response = await api.get(`/contacts/${id}`);

      console.log("CONTACT FETCHED SUCCESSFULLY:", response.data);

      setFormData({
        firstName: response.data.firstName || "",
        lastName: response.data.lastName || "",
        title: response.data.title || "",
        workEmail: response.data.workEmail || "",
        personalEmail: response.data.personalEmail || "",
        workPhone: response.data.workPhone || "",
        homePhone: response.data.homePhone || "",
        personalPhone: response.data.personalPhone || "",
      });

    } catch (err) {
      console.error("FETCH CONTACT ERROR:", err);
      console.error("ERROR RESPONSE:", err.response?.data);
      console.error("ERROR STATUS:", err.response?.status);

      if (err.response?.status === 401) {
        localStorage.removeItem("token");
        localStorage.removeItem("user");

        navigate("/login");
        return;
      }

      setError(
        err.response?.data?.error ||
        "Unable to load contact."
      );

    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // DEBUG LOG 1
    console.log("UPDATE CONTACT BUTTON CLICKED");

    // DEBUG LOG 2
    console.log("CONTACT ID:", id);

    // DEBUG LOG 3
    console.log("UPDATED FORM DATA:", formData);

    try {
      setSaving(true);
      setError("");

      // DEBUG LOG 4
      console.log(
        `SENDING PUT REQUEST TO /api/contacts/${id}...`
      );

      const response = await api.put(
        `/contacts/${id}`,
        formData
      );

      // DEBUG LOG 5
      console.log(
        "CONTACT UPDATED SUCCESSFULLY:",
        response.data
      );

      // After successful update
      navigate(`/contacts/${id}`);

    } catch (err) {
      // DEBUG LOG 6
      console.error("UPDATE CONTACT ERROR:", err);

      console.error(
        "ERROR RESPONSE:",
        err.response?.data
      );

      console.error(
        "ERROR STATUS:",
        err.response?.status
      );

      if (err.response?.status === 401) {
        localStorage.removeItem("token");
        localStorage.removeItem("user");

        navigate("/login");
        return;
      }

      setError(
        err.response?.data?.error ||
        "Unable to update contact."
      );

    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="dashboard">
        <main className="dashboard-content">
          <p>Loading contact...</p>
        </main>
      </div>
    );
  }

  return (
    <div className="dashboard">

      <header className="dashboard-header">

        <div>
          <h1>Contact Management</h1>

          <p>
            Edit Contact
          </p>
        </div>

        <button
          className="logout-button"
          onClick={() => {
            localStorage.removeItem("token");
            localStorage.removeItem("user");

            navigate("/login");
          }}
        >
          Logout
        </button>

      </header>

      <main className="dashboard-content">

        <button
          className="back-button"
          onClick={() =>
            navigate(`/contacts/${id}`)
          }
        >
          ← Back to Contact
        </button>

        <div className="contact-form-card auth-card">

          <h1>Edit Contact</h1>

          <p className="auth-subtitle">
            Update your contact information
          </p>

          {error && (
            <div className="error-message">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>

            <div className="form-row">

              <div className="form-group">
                <label>First Name</label>

                <input
                  type="text"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-group">
                <label>Last Name</label>

                <input
                  type="text"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                  required
                />
              </div>

            </div>

            <div className="form-group">
              <label>Title</label>

              <input
                type="text"
                name="title"
                value={formData.title}
                onChange={handleChange}
              />
            </div>

            <h3>Emails</h3>

            <div className="form-group">
              <label>Work Email</label>

              <input
                type="email"
                name="workEmail"
                value={formData.workEmail}
                onChange={handleChange}
              />
            </div>

            <div className="form-group">
              <label>Personal Email</label>

              <input
                type="email"
                name="personalEmail"
                value={formData.personalEmail}
                onChange={handleChange}
              />
            </div>

            <h3>Phone Numbers</h3>

            <div className="form-group">
              <label>Work Phone</label>

              <input
                type="text"
                name="workPhone"
                value={formData.workPhone}
                onChange={handleChange}
              />
            </div>

            <div className="form-group">
              <label>Home Phone</label>

              <input
                type="text"
                name="homePhone"
                value={formData.homePhone}
                onChange={handleChange}
              />
            </div>

            <div className="form-group">
              <label>Personal Phone</label>

              <input
                type="text"
                name="personalPhone"
                value={formData.personalPhone}
                onChange={handleChange}
              />
            </div>

            <div className="form-actions">

              <button
                type="button"
                onClick={() =>
                  navigate(`/contacts/${id}`)
                }
              >
                Cancel
              </button>

              <button
                type="submit"
                className="auth-button"
                disabled={saving}
              >
                {saving
                  ? "Updating..."
                  : "Update Contact"}
              </button>

            </div>

          </form>

        </div>

      </main>

    </div>
  );
}

export default EditContact;

