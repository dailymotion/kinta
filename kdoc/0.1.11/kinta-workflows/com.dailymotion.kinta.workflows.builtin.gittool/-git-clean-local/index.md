[kinta-workflows](../../index.md) / [com.dailymotion.kinta.workflows.builtin.gittool](../index.md) / [GitCleanLocal](./index.md)

# GitCleanLocal

`open class GitCleanLocal : CliktCommand`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `GitCleanLocal(gitTool: GitTool)` |

### Properties

| Name | Summary |
|---|---|
| [all](all.md) | `val all: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [gitTool](git-tool.md) | `val gitTool: GitTool` |
| [remote](remote.md) | `val remote: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| Name | Summary |
|---|---|
| [run](run.md) | `open fun run(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [GithubCleanLocal](../-github-clean-local.md) | `object GithubCleanLocal : `[`GitCleanLocal`](./index.md) |
| [GitlabCleanLocal](../-gitlab-clean-local.md) | `object GitlabCleanLocal : `[`GitCleanLocal`](./index.md) |
