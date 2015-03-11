package us.kirchmeier.capsule.task

class MavenCapsule extends Capsule {

  MavenCapsule() {
    capsuleConfiguration = project.configurations.mavenCapsule
    applicationSource = project.sourceSets.main.output
    capsuleManifest {
      dependencyConfiguration = project.configurations.runtime
      caplets << 'MavenCapsule'
    }
  }
}
