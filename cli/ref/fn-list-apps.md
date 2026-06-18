# `fn list apps`

```c
$ fn list apps

MANAGEMENT COMMAND
  fn list apps - List all created applications
    
USAGE
  fn [global options] list apps [command options] 
    
DESCRIPTION
  This command provides a list of defined applications.
    
COMMAND OPTIONS
  --cursor value           Pagination cursor
  -n value                 Number of apps to return (default: 100)
  --output value           Output format (json)
  --from-json value        Structured JSON input for command parameters
  --display-name value     Filter applications by exact display name
  --id value               Filter applications by OCID
  --lifecycle-state value  Filter applications by lifecycle state
  --sort-by value          Sort applications by supported field
  --sort-order value       Sort order for list results
  
```

## Examples

List apps filtered by display name:

```bash
fn list apps --display-name parity-app
```

List apps with sorting:

```bash
fn list apps --sort-by displayName --sort-order ASC
```

List apps from JSON input:

```bash
fn list apps --from-json '{"displayName":"parity-app","sortBy":"displayName","sortOrder":"ASC"}'
```

