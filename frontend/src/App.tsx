import './index.css'
import { useEffect, useMemo, useState } from 'react'
import {
  Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis, CartesianGrid, BarChart, Bar
} from 'recharts'

type Intervention = {
  interventionPk: { equipementId: number; technicienId: number; dateOuverture: string }
  dateCloture?: string | null
  priorite: string
  statut: string
  equipement?: { id: number; code: string; site: string }
  technicien?: { id: number; nom: string }
}

type StatsResponse = {
  mttrDays: number
  incidentsPerMonth: { month: number; count: number }[]
}

const API_URL = 'http://localhost:8080/api'

export default function App() {
  const [interventions, setInterventions] = useState<Intervention[]>([])
  const [stats, setStats] = useState<StatsResponse | null>(null)
  const [loading, setLoading] = useState(true)
  const [filters, setFilters] = useState({ priorite: '', statut: '', technicienId: '', site: '' })

  useEffect(() => {
    const controller = new AbortController()
    const params = new URLSearchParams()
    Object.entries(filters).forEach(([k, v]) => { if (v) params.set(k, v as string) })
    Promise.all([
      fetch(`${API_URL}/interventions?${params.toString()}`, { signal: controller.signal }).then(r => r.json()),
      fetch(`${API_URL}/interventions/stats`, { signal: controller.signal }).then(r => r.json())
    ])
      .then(([ints, st]) => { setInterventions(ints); setStats(st) })
      .finally(() => setLoading(false))
    return () => controller.abort()
  }, [filters])

  const monthlyData = useMemo(() => {
    const base = Array.from({ length: 12 }, (_, i) => ({ month: i + 1, count: 0 }))
    if (!stats) return base
    const map = new Map(stats.incidentsPerMonth.map(m => [m.month, m.count]))
    return base.map(b => ({ ...b, count: Number(map.get(b.month) || 0) }))
  }, [stats])

  return (
    <div className="min-h-screen">
      <header className="sticky top-0 z-10 backdrop-blur bg-white/70 border-b border-border">
        <div className="mx-auto max-w-7xl px-4 py-4 flex items-center justify-between">
          <h1 className="text-2xl font-semibold">Maintenance Dashboard</h1>
          <div className="text-sm text-muted-foreground">MTTR: {stats?.mttrDays?.toFixed(1) ?? '—'} days</div>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-4 py-6 space-y-6 animate-fadeIn">
        <section className="grid grid-cols-1 md:grid-cols-4 gap-3">
          <select className="border rounded-md px-3 py-2" value={filters.priorite} onChange={e => setFilters(s => ({ ...s, priorite: e.target.value }))}>
            <option value="">All Priorities</option>
            <option value="LOW">LOW</option>
            <option value="MEDIUM">MEDIUM</option>
            <option value="HIGH">HIGH</option>
            <option value="CRITICAL">CRITICAL</option>
          </select>
          <select className="border rounded-md px-3 py-2" value={filters.statut} onChange={e => setFilters(s => ({ ...s, statut: e.target.value }))}>
            <option value="">All Statuses</option>
            <option value="OPEN">OPEN</option>
            <option value="IN_PROGRESS">IN_PROGRESS</option>
            <option value="CLOSED">CLOSED</option>
          </select>
          <input className="border rounded-md px-3 py-2" placeholder="Technician ID" value={filters.technicienId} onChange={e => setFilters(s => ({ ...s, technicienId: e.target.value }))} />
          <input className="border rounded-md px-3 py-2" placeholder="Site" value={filters.site} onChange={e => setFilters(s => ({ ...s, site: e.target.value }))} />
        </section>

        <section className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="col-span-2 rounded-lg border border-border bg-white p-4">
            <h2 className="font-medium mb-3">Incidents per Month</h2>
            <div className="h-64">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={monthlyData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="month" />
                  <YAxis allowDecimals={false} />
                  <Tooltip />
                  <Bar dataKey="count" fill="#3b82f6" radius={[6,6,0,0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
          <div className="rounded-lg border border-border bg-white p-4">
            <h2 className="font-medium mb-3">MTTR (days)</h2>
            <div className="h-64">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={[{ x: 1, y: stats?.mttrDays ?? 0 }]}>
                  <XAxis dataKey="x" hide />
                  <YAxis domain={[0, Math.max(1, Math.ceil((stats?.mttrDays ?? 0) + 1))]} />
                  <Tooltip />
                  <Line type="monotone" dataKey="y" stroke="#22c55e" strokeWidth={3} dot={{ r: 6 }} />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </div>
        </section>

        <section className="rounded-lg border border-border bg-white overflow-hidden">
          <div className="px-4 py-3 border-b border-border flex items-center justify-between">
            <h2 className="font-medium">Interventions</h2>
            <span className="text-sm text-muted-foreground">{interventions.length} results</span>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-muted/50">
                <tr>
                  <th className="text-left px-4 py-2">Opened</th>
                  <th className="text-left px-4 py-2">Closed</th>
                  <th className="text-left px-4 py-2">Priority</th>
                  <th className="text-left px-4 py-2">Status</th>
                  <th className="text-left px-4 py-2">Technician</th>
                  <th className="text-left px-4 py-2">Site</th>
                </tr>
              </thead>
              <tbody>
                {loading && (
                  <tr><td className="px-4 py-4" colSpan={6}>Loading…</td></tr>
                )}
                {!loading && interventions.length === 0 && (
                  <tr><td className="px-4 py-4" colSpan={6}>No interventions found.</td></tr>
                )}
                {interventions.map((i, idx) => (
                  <tr key={idx} className="border-t border-border">
                    <td className="px-4 py-2">{i.interventionPk?.dateOuverture}</td>
                    <td className="px-4 py-2">{i.dateCloture ?? '—'}</td>
                    <td className="px-4 py-2">{i.priorite}</td>
                    <td className="px-4 py-2">{i.statut}</td>
                    <td className="px-4 py-2">{i.technicien?.nom ?? i.interventionPk?.technicienId}</td>
                    <td className="px-4 py-2">{i.equipement?.site}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      </main>
    </div>
  )
}
