workflow "check build on push" {
  on = "push"
  resolves = ["gradle build"]
}

action "gradle build" {
  uses = "MrRamych/gradle-actions@12909e7ccd3ed7e3b39c2f3ac350d6849eabeaf3"
  args = "--no-daemon build"
}

workflow "process pull request" {
  on = "pull_request"
  resolves = ["branch cleanup"]
}

action "post gif on fail" {
  uses = "jessfraz/shaking-finger-action@master"
  secrets = ["GITHUB_TOKEN"]
}

action "branch cleanup" {
  uses = "jessfraz/branch-cleanup-action@master"
  secrets = ["GITHUB_TOKEN"]
}
