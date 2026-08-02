# MPS Rollback Plan

1. Stop new traffic or place the service in maintenance mode.
2. Preserve application, proxy, database and audit logs.
3. Stop the failed release.
4. Redeploy the previously approved immutable images.
5. If migrations are backward-compatible, retain the database and verify the prior application.
6. If migrations are not safely reversible, restore the pre-release database and document backup.
7. Run the post-deployment verification suite.
8. Notify stakeholders and open an incident record.
9. Do not retry the failed release until root cause and corrective actions are documented.

Database restoration is a controlled last resort because it may discard post-deployment transactions.
