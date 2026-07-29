
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

function AddContact() {
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

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // DEBUG LOG 1: Check if Save Contact button triggers submit
    console.log("SAVE CONTACT BUTTON CLICKED");

    // DEBUG LOG 2: Check form data
    console.log("FORM DATA:", formData);

    setError("");
    setLoading(true);

    try {
      // DEBUG LOG 3: Check if POST request is being sent
      console.log("SENDING POST REQUEST TO /api/contacts...");

      const response = await api.post("/contacts", formData);

      // DEBUG LOG 4: Check successful response
      console.log("CONTACT CREATED SUCCESSFULLY:", response.data);

      // Contact successfully created
      navigate("/dashboard");

    } catch (err) {
      // DEBUG LOG 5: Check error
      console.error("CREATE CONTACT ERROR:", err);

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
        "Unable to create contact. Please try again."
      );

    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">

      <div className="auth-card contact-form-card">

        <h1>Add New Contact</h1>

        <p className="auth-subtitle">
          Add a new contact to your contact management system
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
                placeholder="Enter first name"
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
                placeholder="Enter last name"
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
              placeholder="e.g. Software Engineer"
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
              placeholder="work@example.com"
            />
          </div>

          <div className="form-group">
            <label>Personal Email</label>

            <input
              type="email"
              name="personalEmail"
              value={formData.personalEmail}
              onChange={handleChange}
              placeholder="personal@example.com"
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
              placeholder="Enter work phone"
            />
          </div>

          <div className="form-group">
            <label>Home Phone</label>

            <input
              type="text"
              name="homePhone"
              value={formData.homePhone}
              onChange={handleChange}
              placeholder="Enter home phone"
            />
          </div>

          <div className="form-group">
            <label>Personal Phone</label>

            <input
              type="text"
              name="personalPhone"
              value={formData.personalPhone}
              onChange={handleChange}
              placeholder="Enter personal phone"
            />
          </div>

          <div className="form-actions">

            <button
              type="button"
              onClick={() => navigate("/dashboard")}
            >
              Cancel
            </button>

            <button
              type="submit"
              className="auth-button"
              disabled={loading}
            >
              {loading
                ? "Saving..."
                : "Save Contact"}
            </button>

          </div>

        </form>

      </div>

    </div>
  );
}

export default AddContact;

