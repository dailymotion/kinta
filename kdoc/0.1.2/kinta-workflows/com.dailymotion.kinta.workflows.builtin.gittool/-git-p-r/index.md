[kinta-workflows](../../index.md) / [com.dailymotion.kinta.workflows.builtin.gittool](../index.md) / [GitPR](./index.md)

# GitPR

`open class GitPR : CliktCommand`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `GitPR(gitTool: GitTool)` |

### Properties

| Name | Summary |
|---|---|
| [gitTool](git-tool.md) | `val gitTool: GitTool` |

### Functions

| Name | Summary |
|---|---|
| [run](run.md) | `open fun run(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [GithubPR](../-github-p-r.md) | `object GithubPR : `[`GitPR`](./index.md) |
| [GitlabPR](../-gitlab-p-r.md) | `object GitlabPR : `[`GitPR`](./index.md) |
