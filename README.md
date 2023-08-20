## Simulate chaos test with spring boot app to postgres

Getting the following error
`org.postgresql.util.PSQLException: The connection attempt failed.
at org.postgresql.core.v3.ConnectionFactoryImpl.openConnectionImpl(ConnectionFactoryImpl.java:354) ~[postgresql-42.6.0.jar:42.6.0]`

If i comment proxied datasioource `registry.add("spring.datasource.url", () ->  proxyJdbcUrl)`
and replace with default one `registry.add("spring.datasource.url", () ->  postgres.getJdbcUrl)` then it works fine

But without adding proxied datsource cannot do any chaos testing. 