package us.kirchmeier.capsule.task

class MavenCapsule extends Capsule {

  MavenCapsule() {
    capsuleConfiguration = project.configurations.mavenCapsule
    applicationSource = project.tasks.findByName('jar')
    capsuleManifest {
      dependencyConfiguration = project.configurations.runtime
      caplets << 'MavenCapsule'
    }
  }
}
