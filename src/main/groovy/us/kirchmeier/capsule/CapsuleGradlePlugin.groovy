package us.kirchmeier.capsule

import org.gradle.api.Plugin
import org.gradle.api.Project
import us.kirchmeier.capsule.task.Capsule
import us.kirchmeier.capsule.task.FatCapsule
import us.kirchmeier.capsule.task.MavenCapsule

class CapsuleGradlePlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {
    project.with {
      apply(plugin: 'java')
      ext.Capsule = Capsule.class
      ext.FatCapsule = FatCapsule.class
      ext.MavenCapsule = MavenCapsule.class

      configurations.create('capsule')
      configurations.create('mavenCapsule')
      dependencies {
        capsule 'co.paralleluniverse:capsule:1.0-rc1'
        mavenCapsule 'co.paralleluniverse:capsule-maven:1.0-rc1'
      }
    }
  }
}
