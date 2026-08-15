import { useEffect, useState } from "react";
import { Line, Bar, Doughnut } from "react-chartjs-2";
import Chart from "chart.js/auto";
import api from "../services/api";

function Analytics() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let mounted = true;

    async function fetchAnalytics() {
      try {
        setLoading(true);
        setError("");

        const res = await api.get("/analytics");
        if (mounted) setData(res.data);
      } catch (e) {
        let msg = e?.response?.data ?? e?.message ?? "Failed to load analytics";
        if (typeof msg === "object") {
          msg = msg?.error ?? msg?.message ?? JSON.stringify(msg);
        }
        if (mounted) setError(msg);
      } finally {
        if (mounted) setLoading(false);
      }
    }

    fetchAnalytics();
    return () => (mounted = false);
  }, []);

  if (loading) return <div style={{padding:20}}>Loading analytics…</div>;
  if (error) return <div style={{padding:20,color:'var(--danger)'}}>Error: {error}</div>;

  const areaData = {
    labels: data.months,
    datasets: [
      {
        label: "Sales",
        data: data.revenueMonthly,
        fill: true,
        backgroundColor: 'rgba(20,184,166,0.12)',
        borderColor: 'rgba(20,184,166,0.9)',
        tension: 0.4,
      },
    ],
  };

  const barData = {
    labels: data.activeMonths,
    datasets: [
      {
        label: "Active Users",
        data: data.activeUsers,
        backgroundColor: 'rgba(30,58,91,0.9)',
      },
    ],
  };

  const doughnutData = {
    labels: ["On Track", "At Risk"],
    datasets: [
      {
        data: data.projectStatus,
        backgroundColor: ["var(--primary)", "var(--sidebar-bg)"],
      },
    ],
  };

  return (
    <div style={{display:'grid', gridTemplateColumns:'2fr 1fr', gap:18}}>

      <div style={{background:'var(--surface)', border:'1px solid var(--border)', borderRadius:16, padding:18, boxShadow:'var(--shadow-xs)'}}>
        <div style={{fontWeight:700, color:'var(--heading-color)', marginBottom:8}}>Revenue Growth</div>
        <div style={{height:220}}>
          <Line data={areaData} options={{plugins:{legend:{display:false}}, scales:{y:{grid:{color:'rgba(16,24,40,0.04)'}}}}} />
        </div>
      </div>

      <div style={{display:'flex', flexDirection:'column', gap:12}}>
        <div style={{background:'var(--surface)', border:'1px solid var(--border)', borderRadius:12, padding:18, minHeight:120}}>
          <div style={{fontSize:14, color:'var(--heading-color)', marginBottom:6}}>TOTAL REVENUE</div>
          <div style={{fontSize:28, fontWeight:800}}> ${data.totalRevenue.toLocaleString()} </div>
        </div>

        <div style={{background:'var(--surface)', border:'1px solid var(--border)', borderRadius:12, padding:18}}>
          <div style={{fontSize:14, color:'var(--heading-color)', marginBottom:6}}>NEW USERS</div>
          <div style={{fontSize:22, fontWeight:700}}> +{data.newUsersPercent}% </div>
        </div>

        <div style={{background:'var(--surface)', border:'1px solid var(--border)', borderRadius:12, padding:18}}>
          <div style={{fontSize:14, color:'var(--heading-color)', marginBottom:6}}>TASKS COMPLETED</div>
          <div style={{fontSize:22, fontWeight:700}}> {data.tasksCompletedPercent}% </div>
          <div style={{height:8, background:'rgba(16,24,40,0.06)', borderRadius:8, marginTop:12}}>
            <div style={{width:`${data.tasksCompletedPercent}%`, height:8, background:'var(--primary)', borderRadius:8}} />
          </div>
        </div>

      </div>

      <div style={{gridColumn:'1 / -1', display:'grid', gridTemplateColumns:'1fr 1fr 1fr', gap:18, marginTop:8}}>
        <div style={{background:'var(--surface)', border:'1px solid var(--border)', borderRadius:12, padding:16}}>
          <div style={{fontWeight:700, color:'var(--heading-color)', marginBottom:8}}>Active Users</div>
          <div style={{height:120}}>
            <Bar data={barData} options={{plugins:{legend:{display:false}}, scales:{y:{beginAtZero:true}}}} />
          </div>
        </div>

        <div style={{background:'var(--surface)', border:'1px solid var(--border)', borderRadius:12, padding:16, display:'flex', flexDirection:'column', justifyContent:'center', alignItems:'center'}}>
          <div style={{fontWeight:700, color:'var(--heading-color)'}}>Tasks Completed</div>
          <div style={{fontSize:36, fontWeight:800, marginTop:8}}>{data.tasksCompletedPercent}%</div>
          <div style={{height:8, width:'100%', marginTop:12, background:'rgba(16,24,40,0.06)', borderRadius:8}}>
            <div style={{width:`${data.tasksCompletedPercent}%`, height:8, background:'var(--primary)', borderRadius:8}} />
          </div>
        </div>

        <div style={{background:'var(--surface)', border:'1px solid var(--border)', borderRadius:12, padding:16, display:'flex', flexDirection:'column', justifyContent:'center', alignItems:'center'}}>
          <div style={{fontWeight:700, color:'var(--heading-color)'}}>Project Status</div>
          <div style={{width:120, height:120, marginTop:8}}>
            <Doughnut data={doughnutData} options={{plugins:{legend:{position:'bottom'}}}} />
          </div>
        </div>
      </div>

    </div>
  );
}

export default Analytics;
