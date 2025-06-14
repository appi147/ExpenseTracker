import { Link } from 'react-router-dom';

const NotFound = () => {
  return (
    <div style={styles.container}>
      <h1 style={styles.code}>404</h1>
      <p style={styles.message}>Oops! Page not found.</p>
      <Link to="/" style={styles.link}>Go to Dashboard</Link>
    </div>
  );
};

const styles = {
  container: {
    textAlign: 'center' as const,
    marginTop: '100px',
  },
  code: {
    fontSize: '72px',
    margin: 0,
  },
  message: {
    fontSize: '20px',
    color: '#666',
  },
  link: {
    marginTop: '20px',
    display: 'inline-block',
    color: '#007bff',
    textDecoration: 'none',
  },
};

export default NotFound;
