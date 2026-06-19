# `fn create app`

```c
$ fn create app

MANAGEMENT COMMAND
  fn create app - Create a new application
    
USAGE
  fn [global options] create app [command options] <app-name> 
    
DESCRIPTION
  This command creates a new application.
  Fn supports grouping functions into a set that defines an application (or API), making it easy to organize and deploy.
  Applications define a namespace to organize functions and can contain configuration values that are shared across all functions in that application.
    
COMMAND OPTIONS
  --config value      Application configuration
  --annotation value  Application annotations
  --syslog-url value  Syslog URL to send application logs to
  --subnet-id value   Subnet OCID (repeatable for multiple subnets)
  --tag value         Freeform tag in key=value format (repeatable)
  --defined-tag value Defined tag in namespace.key=value format (repeatable)
  --network-security-group-ids value  OCI NSG OCID (repeatable)
  --trace-config value OCI trace config JSON or file:// path
  --image-policy-config value OCI image policy config JSON or file:// path
  --security-attributes value OCI security attributes JSON or file:// path
  --from-json value    Structured JSON input for command parameters
  --wait-for-state value         Wait until resource reaches lifecycle state
  --max-wait-seconds value       Max waiter duration in seconds
  --wait-interval-seconds value  Waiter poll interval in seconds
  
```

## Examples

Create an app with subnet and tags:

```bash
fn create app my-app \
  --subnet-id ocid1.subnet.oc1..aaaa \
  --tag Department=Finance \
  --defined-tag Operations.CostCenter=42
```

Create an app with advanced OCI parity options:

```bash
fn create app parity-app \
  --subnet-id ocid1.subnet.oc1..aaaa \
  --network-security-group-ids ocid1.networksecuritygroup.oc1..aaaa \
  --trace-config '{"isEnabled":true,"domainId":"ocid1.apmdomain.oc1..aaaa"}'
```

Create from JSON input:

```bash
fn create app json-app --from-json file:///absolute/path/app-create.json
```

