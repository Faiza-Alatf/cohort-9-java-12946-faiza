import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../services/api";

function ContactDetails() {
const { id } = useParams();
const navigate = useNavigate();

const [contact, setContact] = useState(null);
const [loading, setLoading] = useState(true);
const [error, setError] = useState("");
const [deleting, setDeleting] = useState(false);

useEffect(() => {
fetchContact();
}, [id]);

// Fetch Contact Details
const fetchContact = async () => {
try {
setLoading(true);
setError("");

  const response = await api.get(`/contacts/${id}`);

  setContact(response.data);
} catch (err) {
  if (err.response?.status === 401) {
    localStorage.removeItem("token");
    localStorage.removeItem("user");

    navigate("/login");
    return;
  }

  setError(
    err.response?.data?.error ||
    "Unable to load contact details."
  );
} finally {
  setLoading(false);
}


};

// Delete Contact
const handleDelete = async () => {
if (!contact) {
return;
}


const confirmed = window.confirm(
  `Are you sure you want to delete ${contact.firstName} ${contact.lastName}?`
);

if (!confirmed) {
  return;
}

try {
  setDeleting(true);
  setError("");

  await api.delete(`/contacts/${id}`);

  navigate("/dashboard");
} catch (err) {
  if (err.response?.status === 401) {
    localStorage.removeItem("token");
    localStorage.removeItem("user");

    navigate("/login");
    return;
  }

  setError(
    err.response?.data?.error ||
    "Unable to delete contact."
  );

  setDeleting(false);
}

};

// Loading State
if (loading) {
return ( <div className="dashboard"> <main className="dashboard-content"> <p>Loading contact details...</p> </main> </div>
);
}

// Error State
if (error && !contact) {
return ( <div className="dashboard"> <main className="dashboard-content"> <div className="error-message">
{error} </div>


      <button
        className="add-contact-button"
        onClick={() => navigate("/dashboard")}
      >
        Back to Contacts
      </button>
    </main>
  </div>
);


}

// Guard against null contact
if (!contact) {
return null;
}

return ( <div className="dashboard">


  {/* Header */}
  <header className="dashboard-header">
    <div>
      <h1>Contact Management</h1>
      <p>Contact Details</p>
    </div>

    {/* Logout Button */}
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

  {/* Main Content */}
  <main className="dashboard-content">

    {/* Back Button */}
    <button
      className="back-button"
      onClick={() => navigate("/dashboard")}
    >
      ← Back to Contacts
    </button>

    {/* Contact Details Card */}
    <div className="contact-details-card">

      {/* Contact Header */}
      <div className="contact-details-header">
        <div>
          <h2>
            {contact.firstName}{" "}
            {contact.lastName}
          </h2>

          <p>
            {contact.title || "No title"}
          </p>
        </div>
      </div>

      {/* Error Message */}
      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {/* Contact Information */}
      <div className="contact-details-section">
        <h3>Contact Information</h3>

        <div className="details-grid">

          {/* First Name */}
          <div className="detail-item">
            <span>First Name</span>

            <strong>
              {contact.firstName || "N/A"}
            </strong>
          </div>

          {/* Last Name */}
          <div className="detail-item">
            <span>Last Name</span>

            <strong>
              {contact.lastName || "N/A"}
            </strong>
          </div>

          {/* Title */}
          <div className="detail-item">
            <span>Title</span>

            <strong>
              {contact.title || "N/A"}
            </strong>
          </div>

        </div>
      </div>

      {/* Email Addresses */}
      <div className="contact-details-section">
        <h3>Email Addresses</h3>

        <div className="details-grid">

          {/* Work Email */}
          <div className="detail-item">
            <span>Work Email</span>

            <strong>
              {contact.workEmail || "N/A"}
            </strong>
          </div>

          {/* Personal Email */}
          <div className="detail-item">
            <span>Personal Email</span>

            <strong>
              {contact.personalEmail || "N/A"}
            </strong>
          </div>

        </div>
      </div>

      {/* Phone Numbers */}
      <div className="contact-details-section">
        <h3>Phone Numbers</h3>

        <div className="details-grid">

          {/* Work Phone */}
          <div className="detail-item">
            <span>Work Phone</span>

            <strong>
              {contact.workPhone || "N/A"}
            </strong>
          </div>

          {/* Home Phone */}
          <div className="detail-item">
            <span>Home Phone</span>

            <strong>
              {contact.homePhone || "N/A"}
            </strong>
          </div>

          {/* Personal Phone */}
          <div className="detail-item">
            <span>Personal Phone</span>

            <strong>
              {contact.personalPhone || "N/A"}
            </strong>
          </div>

        </div>
      </div>

      {/* Action Buttons */}
      <div className="contact-details-actions">

        {/* Edit Contact Button */}
        <button
          className="edit-contact-button"
          onClick={() =>
            navigate(`/contacts/${contact.id}/edit`)
          }
        >
          Edit Contact
        </button>

        {/* Delete Contact Button */}
        <button
          className="delete-contact-button"
          onClick={handleDelete}
          disabled={deleting}
        >
          {deleting
            ? "Deleting..."
            : "Delete Contact"}
        </button>

      </div>

    </div>
  </main>
</div>


);
}

export default ContactDetails;
