import { Injectable, OnDestroy } from '@angular/core';
import { MediaObserver } from '@angular/flex-layout';
import { Subject, Subscription } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NavService implements OnDestroy {
  mediaChangeSubscription: Subscription;
  screenSize: string = null;
  toggled = false;
  opened = false;
  initialized = false;
  menuTextShowed = false;
  mode = 'side';

  constructor(mediaObserver: MediaObserver) {
    this.mediaChangeSubscription = mediaObserver.asObservable().subscribe(changes => {
      this.screenSize = changes[0].mqAlias;
      if (!this.initialized) {
        this.toggled = this.isLargeScreen();
        this.opened = !this.isSmallScreen();
        this.menuTextShowed = this.isLargeScreen();
        this.initialized = true;
      }
      if (this.mode === 'over' && this.toggled && this.opened) {
        this.mode = this.isLargeScreen() ? 'side' : 'over';
      } else {
        this.opened = !this.isSmallScreen();
        this.menuTextShowed = this.isLargeScreen();
        this.toggled = this.opened && this.menuTextShowed;
      }
    });
  }

  private isLargeScreen(): boolean {
    return (this.screenSize === 'lg' || this.screenSize === 'xl');
  }

  private isMediumScreen(): boolean {
    return (this.screenSize === 'sm' || this.screenSize === 'md');
  }

  private isSmallScreen(): boolean {
    return (this.screenSize === 'xs');
  }

  ngOnDestroy(): void {
    this.mediaChangeSubscription.unsubscribe();
  }

  toggle(): void {
    this.toggled = !this.toggled;
    if (this.toggled) {
      if (!this.isLargeScreen() && !this.opened) {
        this.mode = 'over';
      }
      this.menuTextShowed = true;
      this.opened = true;
    } else {
      if (this.isSmallScreen() || this.mode === 'over') {
        this.opened = false;
      } else {
        this.menuTextShowed = false;
        this.opened = true;
      }
    }
  }
}
