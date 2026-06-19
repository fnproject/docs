# `fn update app`

```c
$ fn update app

MANAGEMENT COMMAND
  fn update app - Update an application
    
USAGE
  fn [global options] update app [command options] <app-name> 
    
DESCRIPTION
  This command updates a created application.
    
COMMAND OPTIONS
  --config value, -c value          Application configuration
  --annotation value                Application annotations
  --tag value                       Freeform tag in key=value form (repeatable)
  --defined-tag value               Defined tag in namespace.key=value form (repeatable)
  --remove-tag value                Remove a freeform tag by key (repeatable)
  --remove-defined-tag value        Remove a defined tag by namespace.key (repeatable)
  --clear-tags                      Clear all freeform and defined tags
  --clear-freeform-tags             Clear all freeform tags
  --clear-defined-tags              Clear all defined tags
  --syslog-url value                Syslog URL to send application logs to
  --subnet-id value                 Subnet OCID (accepted for non-Oracle providers; Oracle-backed update currently not supported)
  --from-json value                 Structured JSON input for command parameters
  --if-match value                  Apply optimistic concurrency control using the provided etag
  --wait-for-state value            Wait until resource reaches lifecycle state
  --max-wait-seconds value          Max waiter duration in seconds
  --wait-interval-seconds value     Waiter poll interval in seconds
  --network-security-group-ids value  OCI NSG OCID (repeatable)
  --trace-config value              OCI trace config JSON or file:// path
  --image-policy-config value       OCI image policy config JSON or file:// path
  --security-attributes value       OCI security attributes JSON or file:// path
  
```

## Examples

Update app tags:

```bash
fn update app my-app \
  --tag Team=Payments \
  --remove-tag Department \
  --clear-defined-tags
```

Update advanced OCI app settings:

```bash
fn update app parity-app \
  --image-policy-config file:///absolute/path/image-policy.json \
  --security-attributes '{"oracle-zpr":{"MaxEgressCount":{"value":"42","mode":"enforce"}}}'
```

Update from JSON:

```bash
fn update app my-app --from-json file:///absolute/path/app-create.json
```

