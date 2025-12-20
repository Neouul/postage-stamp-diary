import { ArrowLeft, ChevronRight } from 'lucide-react';

interface SettingsScreenProps {
  onBack: () => void;
  showInfoOnAlbum: boolean;
  onToggleShowInfo: (value: boolean) => void;
  darkMode: boolean;
  onToggleDarkMode: (value: boolean) => void;
  defaultFrameStyle: string;
  onChangeFrameStyle: (style: string) => void;
}

export function SettingsScreen({
  onBack,
  showInfoOnAlbum,
  onToggleShowInfo,
  darkMode,
  onToggleDarkMode,
  defaultFrameStyle,
  onChangeFrameStyle,
}: SettingsScreenProps) {
  return (
    <div className="min-h-screen bg-white">
      {/* Header */}
      <header className="border-b border-gray-100">
        <div className="flex items-center justify-between px-6 pt-12 pb-4">
          <button
            onClick={onBack}
            className="w-10 h-10 flex items-center justify-center rounded-full hover:bg-gray-50 transition-colors"
            aria-label="Back"
          >
            <ArrowLeft className="w-5 h-5 text-gray-900" />
          </button>
          
          <h2 className="text-gray-900">Settings</h2>
          
          <div className="w-10" /> {/* Spacer for centering */}
        </div>
      </header>

      {/* Settings List */}
      <div className="px-6 py-8">
        {/* Show Info on Album */}
        <div className="flex items-center justify-between py-6 border-b border-gray-100">
          <div>
            <div className="text-gray-900 mb-1">Show Info on Album</div>
            <div className="text-sm text-gray-500">Display date and location on stamps</div>
          </div>
          <button
            onClick={() => onToggleShowInfo(!showInfoOnAlbum)}
            className={`relative w-12 h-7 rounded-full transition-colors ${
              showInfoOnAlbum ? 'bg-gray-900' : 'bg-gray-200'
            }`}
            aria-label="Toggle show info"
          >
            <div
              className={`absolute top-1 left-1 w-5 h-5 bg-white rounded-full transition-transform ${
                showInfoOnAlbum ? 'translate-x-5' : 'translate-x-0'
              }`}
            />
          </button>
        </div>

        {/* Default Frame Style */}
        <button
          onClick={() => {
            const styles = ['Classic', 'Modern', 'Vintage', 'Minimal'];
            const currentIndex = styles.indexOf(defaultFrameStyle);
            const nextIndex = (currentIndex + 1) % styles.length;
            onChangeFrameStyle(styles[nextIndex]);
          }}
          className="w-full flex items-center justify-between py-6 border-b border-gray-100 hover:bg-gray-50 transition-colors -mx-6 px-6"
        >
          <div className="text-left">
            <div className="text-gray-900 mb-1">Default Frame Style</div>
            <div className="text-sm text-gray-500">{defaultFrameStyle}</div>
          </div>
          <ChevronRight className="w-5 h-5 text-gray-400" />
        </button>

        {/* Backup & Restore */}
        <button
          onClick={() => {
            alert('Backup & Restore functionality would be implemented here.');
          }}
          className="w-full flex items-center justify-between py-6 border-b border-gray-100 hover:bg-gray-50 transition-colors -mx-6 px-6"
        >
          <div className="text-left">
            <div className="text-gray-900 mb-1">Backup & Restore</div>
            <div className="text-sm text-gray-500">Save your stamps to cloud</div>
          </div>
          <ChevronRight className="w-5 h-5 text-gray-400" />
        </button>

        {/* Dark Mode */}
        <div className="flex items-center justify-between py-6 border-b border-gray-100">
          <div>
            <div className="text-gray-900 mb-1">Dark Mode</div>
            <div className="text-sm text-gray-500">Switch to dark theme</div>
          </div>
          <button
            onClick={() => onToggleDarkMode(!darkMode)}
            className={`relative w-12 h-7 rounded-full transition-colors ${
              darkMode ? 'bg-gray-900' : 'bg-gray-200'
            }`}
            aria-label="Toggle dark mode"
          >
            <div
              className={`absolute top-1 left-1 w-5 h-5 bg-white rounded-full transition-transform ${
                darkMode ? 'translate-x-5' : 'translate-x-0'
              }`}
            />
          </button>
        </div>

        {/* App Info */}
        <div className="mt-12 text-center">
          <div className="text-sm text-gray-400 mb-2" style={{ fontFamily: 'Georgia, serif' }}>
            Momento
          </div>
          <div className="text-xs text-gray-400">
            Version 1.0.0
          </div>
        </div>
      </div>
    </div>
  );
}
