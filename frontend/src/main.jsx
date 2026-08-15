import { Component, StrictMode } from "react";
import { createRoot } from "react-dom/client";

import "./index.css";
import "./App.css";

import App from "./App.jsx";


class ErrorBoundary extends Component {

  constructor(props) {

    super(props);

    this.state = {
      hasError: false,
      error: null,
      errorInfo: null
    };

  }


  static getDerivedStateFromError() {

    return {
      hasError: true
    };

  }


  componentDidCatch(error, errorInfo) {

    console.error(
      "Application rendering error:",
      error,
      errorInfo
    );

    this.setState({ error, errorInfo });

  }


  handleRefresh = () => {

    window.location.reload();

  };


  render() {


    if (this.state.hasError) {


      return (

        <div
          style={{
            minHeight: "100vh",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            background: "var(--background)",
            padding: "20px"
          }}
        >

          <div
            className="card"
            style={{
              maxWidth: "720px",
              padding: "28px",
              textAlign: "left",
              lineHeight: 1.4
            }}
          >

            <h1 style={{marginBottom:6}}>Something went wrong</h1>

            <p style={{marginTop:6}}>
              We couldn't load this page. Please refresh and try again.
            </p>

            <div style={{marginTop:14}}>
              <button
                className="auth-button"
                style={{marginRight:12}}
                onClick={this.handleRefresh}
              >
                Refresh Page
              </button>
              <button
                className="auth-button"
                onClick={() => window.navigator.clipboard?.writeText(JSON.stringify({
                  error: this.state.error?.toString(),
                  stack: this.state.errorInfo?.componentStack
                }))}
              >
                Copy error
              </button>
            </div>

            {this.state.error && (
              <details style={{marginTop:16, whiteSpace:'pre-wrap', background:'#fff', padding:12, borderRadius:8, border:'1px solid #eee'}}>
                <summary style={{cursor:'pointer', fontWeight:600}}>View error details</summary>
                <div style={{marginTop:8, color:'#111'}}>
                  <div><strong>Message:</strong> {this.state.error?.toString()}</div>
                  <div style={{marginTop:8}}><strong>Stack:</strong>
                    <pre style={{fontSize:12, overflowX:'auto'}}>{this.state.errorInfo?.componentStack}</pre>
                  </div>
                </div>
              </details>
            )}

          </div>

        </div>

      );

    }


    return this.props.children;

  }

}



createRoot(
  document.getElementById("root")
).render(

  <StrictMode>

    <ErrorBoundary>

      <App />

    </ErrorBoundary>

  </StrictMode>

);